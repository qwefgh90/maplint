package lint.cli.option;

import java.nio.file.Path;

/**
 * @author qwefgh90
 */
public class LintOption {
    final Path projectPath;
    LogLevel printLevel;
    DataSource dataSource;
    String mappedStatementId;
    String configFileName;

    public LintOption(Path projectPath) {
        this.projectPath = projectPath;
    }

    public Path getProjectPath() {
        return projectPath;
    }

    public LogLevel getPrintLevel() {
        return printLevel;
    }

    public void setPrintLevel(LogLevel printLevel) {
        this.printLevel = printLevel;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getMappedStatementId() {
        return mappedStatementId;
    }

    public void setMappedStatementId(String mappedStatementId) {
        this.mappedStatementId = mappedStatementId;
    }

    public String getConfigFileName() {
        return configFileName;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public static final class LintOptionBuilder {
        Path projectPath;
        LogLevel printLevel;
        DataSource dataSource;
        String mappedStatementId;
        String configFileName;

        private LintOptionBuilder() {
        }

        public static LintOptionBuilder aLintOption(Path projectPath) {
            var builder = new LintOptionBuilder();
            builder.projectPath = projectPath;
            builder.printLevel = LogLevel.Summary;
            return builder;
        }

        public LintOptionBuilder projectPath(Path projectPath) {
            this.projectPath = projectPath;
            return this;
        }

        public LintOptionBuilder printLevel(LogLevel printLevel) {
            this.printLevel = printLevel;
            return this;
        }
        public LintOptionBuilder configFileName(String configFileName) {
            this.configFileName = configFileName;
            return this;
        }

        public LintOptionBuilder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public LintOptionBuilder mappedStatementId(String mappedStatementId) {
            this.mappedStatementId = mappedStatementId;
            return this;
        }

        public LintOption build() {
            LintOption lintOption = new LintOption(projectPath);
            lintOption.setPrintLevel(printLevel);
            lintOption.setDataSource(dataSource);
            lintOption.setMappedStatementId(mappedStatementId);
            lintOption.setConfigFileName(configFileName);
            return lintOption;
        }
    }
}
