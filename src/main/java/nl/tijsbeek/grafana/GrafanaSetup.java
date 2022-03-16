package nl.tijsbeek.grafana;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tijsbeek.config.Config;
import nl.tijsbeek.discord.system.CommandHandler;
import nl.tijsbeek.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class GrafanaSetup {
    private static final Logger logger = LoggerFactory.getLogger(GrafanaSetup.class);

    private static final String SLASHCOMMAND_TEMPLATE_NAME = "slashcommand_name";
    private static final String USER_CONTEXTCOMMAND_TEMPLATE_NAME = "user_contextcommand_name";
    private static final String MESSAGE_CONTEXTCOMMAND_TEMPLATE_NAME = "message_contextcommand_name";

    private static final String dataSourceUid = "discord-bot-prometheus";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Config config;

    // eyJrIjoiWTBHTDE4b2phc1Nma3E3U3BncnF3Yjk0YWlxb2VQZUMiLCJuIjoiYSIsImlkIjoxfQ==

    public GrafanaSetup(@NotNull final CommandHandler commandHandler, @NotNull final Config config) throws IOException, InterruptedException {
        this.config = config;

        generateDatasource();
        generateDashboard(commandHandler);
    }

    private HttpRequest.Builder generateRequest(String apiRoute) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + config.getGrafanaPort() + apiRoute))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + config.getGrafanaKey());
    }


    private void generateDatasource() throws IOException, InterruptedException {
        String commandTypeRow = getResourceAsString("datasource.json");

        commandTypeRow = commandTypeRow.replace("{{DATA-SOURCE-NAME}}", dataSourceUid);
        commandTypeRow = commandTypeRow.replace("{{PROMETHEUS-PORT}}", config.getPrometheusPort());

        HttpRequest request = generateRequest("/api/datasources")
                .POST(HttpRequest.BodyPublishers.ofString(commandTypeRow))
                .build();

        String responseBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();

        logger.info(responseBody);
        logger.info(commandTypeRow);
    }


    private void generateDashboard(@NotNull final CommandHandler commandHandler) throws IOException, InterruptedException {

        String rowsSlash = generateRows("slashcommands", SLASHCOMMAND_TEMPLATE_NAME, "slash") + ",";
        String rowsUser = generateRows("user_contextcommands", USER_CONTEXTCOMMAND_TEMPLATE_NAME, "user context") + ",";
        String rowsMessage = generateRows("message_contextcommands", MESSAGE_CONTEXTCOMMAND_TEMPLATE_NAME, "message context");

        String templateSlash = generateTemplate(commandHandler.getSlashCommandCommand(), SLASHCOMMAND_TEMPLATE_NAME) + ",";
        String templateUser = generateTemplate(commandHandler.getUserContextCommand(), USER_CONTEXTCOMMAND_TEMPLATE_NAME) + ",";
        String templateMessage = generateTemplate(commandHandler.getMessageContextCommand(), MESSAGE_CONTEXTCOMMAND_TEMPLATE_NAME);

        String fullJson = formatFullJson(rowsSlash + rowsUser + rowsMessage, templateSlash + templateUser + templateMessage);

        HttpRequest request = generateRequest("/api/dashboards/db")
                .POST(HttpRequest.BodyPublishers.ofString(fullJson))
                .build();

        String responseBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();

        logger.info(responseBody);
        logger.info(fullJson);
    }

    private String formatFullJson(String rows, String templateVariables) throws IOException {
        String dashboardJson = getResourceAsString("dashboard.json");

        dashboardJson = dashboardJson.replace("{{COMMAND-ROWS}}", rows);
        dashboardJson = dashboardJson.replace("{{TEMPLATE-VARIABLE}}", templateVariables);
        dashboardJson = dashboardJson.replace("{{DATA-SOURCE-NAME}}", dataSourceUid);


        String fullJson = getResourceAsString("full-json.json");

        fullJson = fullJson.replace("{{DASHBOARD}}", dashboardJson);

        return fullJson;
    }


    private String generateRows(CharSequence metricCommandType, CharSequence commandTemplateName, CharSequence commandType) throws IOException {
        String commandTypeRow = getResourceAsString("command-type-row.json");

        commandTypeRow = commandTypeRow.replace("{{METRIC-COMMAND-TYPE}}", metricCommandType);
        commandTypeRow = commandTypeRow.replace("{{COMMAND-NAME-TEMPLATE}}", "$" + commandTemplateName);
        commandTypeRow = commandTypeRow.replace("{{COMMAND-TYPE}}", commandType);
        commandTypeRow = commandTypeRow.replace("{{DATA-SOURCE-NAME}}", dataSourceUid);

        return commandTypeRow;
    }

    private String generateTemplate(List<String> commandNames, CharSequence commandTemplateName) throws IOException {
        String commandTemplate = getResourceAsString("template-variable.json");

        commandTemplate = commandTemplate.replace("{{COMMAND-NAME-TEMPLATE}}", commandTemplateName);
        commandTemplate = commandTemplate.replace("{{CURRENT}}", generateCurrent(commandNames));
        commandTemplate = commandTemplate.replace("{{OPTIONS}}", generateTemplateOptions(commandNames));
        commandTemplate = commandTemplate.replace("{{QUERY}}", generateQuery(commandNames));

        return commandTemplate;
    }

    private String generateCurrent(List<String> commandNames) throws JsonProcessingException {
        String firstCommand = commandNames.get(0);

        if (firstCommand == null) {
            return "";
        }

        return toJson(TemplateVariableOption.byName(firstCommand));
    }

    private CharSequence generateQuery(List<String> commandNames) {
        return StreamUtils.toJoinedString(commandNames.stream(), ",");
    }

    private CharSequence generateTemplateOptions(List<String> commandNames) throws JsonProcessingException {
        List<TemplateVariableOption> variables = commandNames.stream()
                .map(TemplateVariableOption::byName)
                .toList();

        TemplateVariableOption variable = variables.get(0);
        if (variable != null) {
            variable.setSelected(true);
        }

        return toJson(variables);
    }


    private String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private static String getResourceAsString(String fileName) throws IOException {
        InputStream inputStream = GrafanaSetup.class.getClassLoader().getResourceAsStream("grafana/" + fileName);

        if (inputStream == null) throw new IOException("couldn't find resource " + fileName);

        String resourceContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        inputStream.close();

        return resourceContent;
    }
}