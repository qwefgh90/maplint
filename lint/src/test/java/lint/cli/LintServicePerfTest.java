package lint.cli;

import lint.cli.option.LintOption;
import mybatis.diagnostics.exception.DatabaseObjectNameCheckException;
import mybatis.parser.XMLConfigParser;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import org.apache.ibatis.session.ExecutorType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @author qwefgh90
 */
public class LintServicePerfTest {
    Logger logger = LoggerFactory.getLogger(LintServicePerfTest.class);

    static void setupMyBatisApp() throws ConfigNotFoundException, IOException, URISyntaxException, SQLException, DatabaseObjectNameCheckException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var server = new MyBatisProjectService();
        server.initialize(root, "h2-perf");
        var path = server.getConfigFile();

        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        var config = parser.parse();
        var env = config.getEnvironment();
        var manager = env.getTransactionManager();
        var transaction = manager
                .getTransactionFactory()
                .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
        var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
        var connection = transaction.getConnection();
        var mapper = config.getMappedStatement("db.PerfMapper.createTableIfNotExist");
        var pstmt = connection.prepareStatement(mapper.getSqlSource().getBoundSql(new HashMap()).toString());
        pstmt.execute();
        connection.close();
    }

    /**
     19:00:13.771 [main] INFO  [LintServicePerfTest.java:133] - {db.PerfMapper.400parameters=
     AverageElapsedTimeBuilder{mapperName='db.PerfMapper.400parameters',
        durationListForSyntaxErrorReport=100, durationListForUnresolvedJavaTypeErrorReport=100, durationListForObjectNameNotFoundErrorReport=100, durationListForIncomptatibleTypeErrorReport=100}, db.PerfMapper.createTableIfNotExist=AverageElapsedTimeBuilder{mapperName='db.PerfMapper.createTableIfNotExist', durationListForSyntaxErrorReport=100, durationListForUnresolvedJavaTypeErrorReport=100, durationListForObjectNameNotFoundErrorReport=0, durationListForIncomptatibleTypeErrorReport=0}, db.PerfMapper.500parameters=
     AverageElapsedTimeBuilder{mapperName='db.PerfMapper.500parameters',
        durationListForSyntaxErrorReport=100, durationListForUnresolvedJavaTypeErrorReport=100, durationListForObjectNameNotFoundErrorReport=100, durationListForIncomptatibleTypeErrorReport=100}, db.PerfMapper.100parameters=
     AverageElapsedTimeBuilder{mapperName='db.PerfMapper.100parameters',
        durationListForSyntaxErrorReport=100, durationListForUnresolvedJavaTypeErrorReport=100, durationListForObjectNameNotFoundErrorReport=100, durationListForIncomptatibleTypeErrorReport=100}, db.PerfMapper.200parameters=
     AverageElapsedTimeBuilder{mapperName='db.PerfMapper.200parameters',
        durationListForSyntaxErrorReport=100, durationListForUnresolvedJavaTypeErrorReport=100, durationListForObjectNameNotFoundErrorReport=100, durationListForIncomptatibleTypeErrorReport=100}, db.PerfMapper.300parameters=
     AverageElapsedTimeBuilder{mapperName='db.PerfMapper.300parameters',
        durationListForSyntaxErrorReport=100, durationListForUnresolvedJavaTypeErrorReport=100, durationListForObjectNameNotFoundErrorReport=100, durationListForIncomptatibleTypeErrorReport=100}}
     19:00:13.781 [main] INFO  [LintServicePerfTest.java:142] -
     📛 mapperName='db.PerfMapper.100parameters'
     ⌛ syntaxErrorReport=0.00295939800
     ⌛ unresolvedJavaTypeErrorReport=0.00001999000
     ⌛ objectNameNotFoundErrorReport=0.00650000800
     ⌛ incomptatibleTypeErrorReport=0.00729154800
     19:00:13.781 [main] INFO  [LintServicePerfTest.java:142] -
     📛 mapperName='db.PerfMapper.200parameters'
     ⌛ syntaxErrorReport=0.00432844700
     ⌛ unresolvedJavaTypeErrorReport=0.00002999400
     ⌛ objectNameNotFoundErrorReport=0.00945380000
     ⌛ incomptatibleTypeErrorReport=0.01372020400
     19:00:13.782 [main] INFO  [LintServicePerfTest.java:142] -
     📛 mapperName='db.PerfMapper.300parameters'
     ⌛ syntaxErrorReport=0.00630977700
     ⌛ unresolvedJavaTypeErrorReport=0.00004999700
     ⌛ objectNameNotFoundErrorReport=0.01374403300
     ⌛ incomptatibleTypeErrorReport=0.02014991200
     19:00:13.782 [main] INFO  [LintServicePerfTest.java:142] -
     📛 mapperName='db.PerfMapper.400parameters'
     ⌛ syntaxErrorReport=0.00793000000
     ⌛ unresolvedJavaTypeErrorReport=0.00004999100
     ⌛ objectNameNotFoundErrorReport=0.01711101700
     ⌛ incomptatibleTypeErrorReport=0.02719055500
     19:00:13.782 [main] INFO  [LintServicePerfTest.java:142] -
     📛 mapperName='db.PerfMapper.500parameters'
     ⌛ syntaxErrorReport=0.01016007100
     ⌛ unresolvedJavaTypeErrorReport=0.00013001500
     ⌛ objectNameNotFoundErrorReport=0.02137632600
     ⌛ incomptatibleTypeErrorReport=0.03527741900
     19:00:13.783 [main] INFO  [LintServicePerfTest.java:142] -
     * @throws URISyntaxException
     * @throws SQLException
     * @throws ConfigNotFoundException
     * @throws DatabaseObjectNameCheckException
     * @throws IOException
     * @throws MyBatisProjectInitializationException
     */
    @Disabled
    @Test
    void performanceTesting() throws URISyntaxException, SQLException, ConfigNotFoundException, DatabaseObjectNameCheckException, IOException, MyBatisProjectInitializationException {
        setupMyBatisApp();
        LintService lintService = new LintService();
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var lintReportList = new ArrayList<LintReport>();
        for (var i = 0; i < 1; i++) {
            lintReportList.add(lintService.lint(LintOption.LintOptionBuilder.aLintOption(root).configFileName("h2-perf").build()));
        }
        var builderMap = new HashMap<String, AverageElapsedTimeBuilder>();
        for (var report : lintReportList) {
            for (var mapperReport : report.diagnosisReport.getReportList()) {
                var builder = builderMap.computeIfAbsent(mapperReport.getMapperStatement().getId(),
                        (key) -> new AverageElapsedTimeBuilder(key));
                if (mapperReport.getSyntaxErrorReport() != null)
                    builder.aggregateSyntaxErrorReport(mapperReport.getSyntaxErrorReport().getElapsedTime());
                if (mapperReport.getUnresolvedJavaTypeErrorReport() != null)
                    builder.aggregateUnresolvedJavaTypeErrorReport(mapperReport.getUnresolvedJavaTypeErrorReport().getElapsedTime());
                if (mapperReport.getObjectNameNotFoundErrorReport() != null)
                    builder.aggregateObjectNameNotFoundErrorReport(mapperReport.getObjectNameNotFoundErrorReport().getElapsedTime());
                if (mapperReport.getIncomptatibleTypeErrorReport() != null)
                    builder.aggregateIncomptatibleTypeErrorReport(mapperReport.getIncomptatibleTypeErrorReport().getElapsedTime());
                ;
            }
        }
        logger.info(builderMap.toString());
        var results = new ArrayList<AverageElapsedTimeBuilder>(builderMap.values());
        results.sort(new Comparator<AverageElapsedTimeBuilder>() {
            @Override
            public int compare(AverageElapsedTimeBuilder o1, AverageElapsedTimeBuilder o2) {
                return o1.mapperName.compareTo(o2.mapperName);
            }
        });
        for(var result : results){
            logger.info(result.build().toString());
        }
    }

    class AverageElapsedTime {
        String mapperName;
        long syntaxErrorReport;
        long unresolvedJavaTypeErrorReport;
        long objectNameNotFoundErrorReport;
        long incomptatibleTypeErrorReport;

        public AverageElapsedTime(String mapperName, long syntaxErrorReport, long unresolvedJavaTypeErrorReport, long objectNameNotFoundErrorReport, long incomptatibleTypeErrorReport) {
            this.mapperName = mapperName;
            this.syntaxErrorReport = syntaxErrorReport;
            this.unresolvedJavaTypeErrorReport = unresolvedJavaTypeErrorReport;
            this.objectNameNotFoundErrorReport = objectNameNotFoundErrorReport;
            this.incomptatibleTypeErrorReport = incomptatibleTypeErrorReport;
        }

        @Override
        public String toString() {
            return "\n\uD83D\uDCDB mapperName='" + mapperName + '\'' +
                    "\n ⌛ syntaxErrorReport=" + toSecondWithNubmerFormat(syntaxErrorReport) +
                    "\n ⌛ unresolvedJavaTypeErrorReport=" + toSecondWithNubmerFormat(unresolvedJavaTypeErrorReport) +
                    "\n ⌛ objectNameNotFoundErrorReport=" + toSecondWithNubmerFormat(objectNameNotFoundErrorReport) +
                    "\n ⌛ incomptatibleTypeErrorReport=" + toSecondWithNubmerFormat(incomptatibleTypeErrorReport)
                    ;
        }

        String toSecondWithNubmerFormat(long number){
            double sec = (((double)number) / 1_000_000_000);
            NumberFormat formatter = new DecimalFormat("0.00000000000");
            String string = formatter.format(sec);
            return string;
        }
    }

    class AverageElapsedTimeBuilder {
        String mapperName;
        List<Duration> durationListForSyntaxErrorReport = new ArrayList<>();
        List<Duration> durationListForUnresolvedJavaTypeErrorReport = new ArrayList<>();
        List<Duration> durationListForObjectNameNotFoundErrorReport = new ArrayList<>();
        List<Duration> durationListForIncomptatibleTypeErrorReport = new ArrayList<>();

        public AverageElapsedTimeBuilder aggregateSyntaxErrorReport(Duration elapsedTime) {
            durationListForSyntaxErrorReport.add(elapsedTime);
            return this;
        }

        public AverageElapsedTimeBuilder aggregateUnresolvedJavaTypeErrorReport(Duration elapsedTime) {
            durationListForUnresolvedJavaTypeErrorReport.add(elapsedTime);
            return this;
        }

        public AverageElapsedTimeBuilder aggregateObjectNameNotFoundErrorReport(Duration elapsedTime) {
            durationListForObjectNameNotFoundErrorReport.add(elapsedTime);
            return this;
        }

        public AverageElapsedTimeBuilder aggregateIncomptatibleTypeErrorReport(Duration elapsedTime) {
            durationListForIncomptatibleTypeErrorReport.add(elapsedTime);
            return this;
        }

        public AverageElapsedTimeBuilder(String mapperName) {
            this.mapperName = mapperName;
        }

        public AverageElapsedTime build() {
            return new AverageElapsedTime(mapperName, sum(durationListForSyntaxErrorReport), sum(durationListForUnresolvedJavaTypeErrorReport)
                    , sum(durationListForObjectNameNotFoundErrorReport), sum(durationListForIncomptatibleTypeErrorReport));
        }

        protected long sum(List<Duration> list) {
            if(list.size() == 0)
                return -1;
            long sum = 0;
            for (var item : list) {
                sum += item.toNanos();
            }
            return sum / list.size();
        }

        @Override
        public String toString() {
            return "AverageElapsedTimeBuilder{" +
                    "mapperName='" + mapperName + '\'' +
                    ", durationListForSyntaxErrorReport=" + durationListForSyntaxErrorReport.size() +
                    ", durationListForUnresolvedJavaTypeErrorReport=" + durationListForUnresolvedJavaTypeErrorReport.size() +
                    ", durationListForObjectNameNotFoundErrorReport=" + durationListForObjectNameNotFoundErrorReport.size() +
                    ", durationListForIncomptatibleTypeErrorReport=" + durationListForIncomptatibleTypeErrorReport.size() +
                    '}';
        }
    }
}
