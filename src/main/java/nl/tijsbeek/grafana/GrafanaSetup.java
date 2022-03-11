package nl.tijsbeek.grafana;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tijsbeek.config.Config;
import nl.tijsbeek.discord.system.CommandHandler;
import nl.tijsbeek.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class GrafanaSetup {
    public static final String SLASHCOMMAND_TEMPLATE_NAME = "slashcommand_name";
    public static final String USER_CONTEXTCOMMAND_TEMPLATE_NAME = "user_contextcommand_name";
    public static final String MESSAGE_CONTEXTCOMMAND_TEMPLATE_NAME = "message_contextcommand_name";
    private final ObjectMapper objectMapper = new ObjectMapper();

    // eyJrIjoiWTBHTDE4b2phc1Nma3E3U3BncnF3Yjk0YWlxb2VQZUMiLCJuIjoiYSIsImlkIjoxfQ==

    public GrafanaSetup(CommandHandler commandHandler, @NotNull final Config config) throws IOException, InterruptedException {

        String rowsSlash = generateRows("slashcommands", SLASHCOMMAND_TEMPLATE_NAME, "slash") + ",";
        String rowsUser = generateRows("user_contextcommands", USER_CONTEXTCOMMAND_TEMPLATE_NAME, "user context")  + ",";
        String rowsMessage = generateRows("message_contextcommands", MESSAGE_CONTEXTCOMMAND_TEMPLATE_NAME, "message context");

        String templateSlash = generateTemplate(commandHandler.getSlashCommandCommand(), SLASHCOMMAND_TEMPLATE_NAME) + ",";
        String templateUser = generateTemplate(commandHandler.getUserContextCommand(), USER_CONTEXTCOMMAND_TEMPLATE_NAME) + ",";
        String templateMessage = generateTemplate(commandHandler.getMessageContextCommand(), MESSAGE_CONTEXTCOMMAND_TEMPLATE_NAME);

        String fullJson = formatFullJson(rowsSlash + rowsUser + rowsMessage, templateSlash + templateUser + templateMessage);

        System.out.println(fullJson);

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + config.getGrafanaPort() + "/api/dashboards/db"))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + config.getGrafanaKey())
                .POST(HttpRequest.BodyPublishers.ofString(fullJson))
                .build();

        System.out.println(httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                .body());
    }

    private String formatFullJson(String rows, String templateVariables) throws IOException {
        String dashboardJson = getResourceAsString("dashboard.json");

        dashboardJson = dashboardJson.replace("{{COMMAND-ROWS}}", rows);
        dashboardJson = dashboardJson.replace("{{TEMPLATE-VARIABLE}}", templateVariables);

        String fullJson = getResourceAsString("full-json.json");

        fullJson = fullJson.replace("{{DASHBOARD}}", dashboardJson);

        return fullJson;
    }


    private String generateRows(CharSequence metricCommandType, CharSequence commandTemplateName, CharSequence commandType) throws IOException {
        String commandTypeRow = getResourceAsString("command-type-row.json");

        commandTypeRow = commandTypeRow.replace("{{METRIC-COMMAND-TYPE}}", metricCommandType);
        commandTypeRow = commandTypeRow.replace("{{COMMAND-NAME-TEMPLATE}}", "$" + commandTemplateName);
        commandTypeRow = commandTypeRow.replace("{{COMMAND-TYPE}}", commandType);

        return commandTypeRow;
    }

    public String generateTemplate(List<String> commandNames, CharSequence commandTemplateName) throws IOException {
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