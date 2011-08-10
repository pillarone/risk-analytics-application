package org.pillarone.riskanalytics.application.reports.gira.action

import org.pillarone.riskanalytics.application.reports.AbstractReportActionTests

import org.pillarone.riskanalytics.core.simulation.item.Simulation
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.pillarone.riskanalytics.application.reports.ReportHelper
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.reports.gira.model.GiraReportModel

import org.pillarone.riskanalytics.application.reports.comment.action.CommentReportActionTests
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils

import org.pillarone.riskanalytics.application.reports.bean.ReportChartDataBean
import org.pillarone.riskanalytics.application.reports.gira.model.GiraReportHelper
import org.pillarone.riskanalytics.application.reports.JasperChartUtils
import org.pillarone.riskanalytics.application.util.ReportUtils
import org.pillarone.riskanalytics.application.reports.gira.model.ChartDataSourceFactory
import org.joda.time.DateTime

import org.pillarone.riskanalytics.application.ui.util.SeriesColor

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class GiraReportActionTests extends AbstractReportActionTests {
    int gIndex = 100

    void testGeneratePDFReport() {
        GiraSubReportTests.compile()
        //                File testExportFile = File.createTempFile("test", ".pdf")
        println "date ${org.pillarone.riskanalytics.application.ui.util.DateFormatUtils.formatDetailed(new DateTime())}"
        GiraReportAction action = new GiraReportAction()

        action.metaClass.getSelectedItem = {->
            new Simulation("test")
        }



        action.metaClass.getFileName = {->
//            return testExportFile.getAbsolutePath()
            return "test"
        }
        action.metaClass.initReportModel = {Simulation simulation ->

        }

        action.metaClass.saveReport = {def output, String fileName, ULCComponent component ->
            File f = new File("E:/downloads/reports/Gira.pdf")
//            FileOutputStream fos = new FileOutputStream(testExportFile)
            FileOutputStream fos = new FileOutputStream(f)
            fos.write(output)
        }

        GiraReportModel model = new GiraReportModel(null, "GIRA")
        GiraReportHelper reportHelper = new GiraReportHelper()


        reportHelper.metaClass.getComments = {String path, int periodIndex ->
            println " ----------------- get comments"
            List list = []
            for (int i = 0; i < 2; i++) {
                list << CommentReportActionTests.getComment(i)
            }
            return list
        }

        reportHelper.metaClass.addCommentData = {Comment comment, Collection currentValues ->
            String boxTitle = comment.path + " P" + String.valueOf(comment.period)
            String userAndDate = "testUser" + " " + DateFormatUtils.formatDetailed(comment.lastChange)
            String tags = comment.getTags().join(", ")
            String addedFiles = "Attachments: " + comment.getFiles().join(", ")
            currentValues << ["boxTitle": boxTitle, "userAndDate": userAndDate, "tags": tags, "addedFiles": addedFiles, "text": comment.getText()]

        }

        reportHelper.metaClass.getComponentName = {ResultPathParser parser, String path ->
            path
        }

        reportHelper.metaClass.getPeriodLabel = { int p ->
            return p + ""
        }

        model.metaClass.getPaths = {->
            model.parser = new ResultPathParser("GIRA", [])

            Map<PathType, List<String>> map = [:]
            map.put(PathType.CLAIMSGENERATORS, [["path1:outClaims"], ["path2:outClaims"], ["path3:outClaims"], ["path4:outClaims"]] as List<List<String>>)
            return map
        }

        model.metaClass.getWaterfallBeans = {List<List<String>> componentPaths, PathType pathType ->
            []
        }


        ChartDataSourceFactory factory = new ChartDataSourceFactory(reportHelper: reportHelper)

        factory.metaClass.getChartDataSource = { int period, List<String> path ->
            //pdf chart
            Collection currentValues = new ArrayList<ReportChartDataBean>()
            List xyPairs = [[4.002748811660515E7, 0.0], [4.5329557376964346E7, 4.569124947003888E-11], [4.577458619169055E7, 5.5176196307463135E-11], [4.621961500641675E7, 6.63727780349794E-11], [4.666464382114296E7, 7.964074717340532E-11], [4.710967263586916E7, 9.52101769738201E-11], [4.7554701450595364E7, 1.1355928597496376E-10], [4.799973026532157E7, 1.3478715919544572E-10], [4.844475908004777E7, 1.5973807789789445E-10], [4.8889787894773975E7, 1.8860692651505645E-10], [4.933481670950018E7, 2.2196777521501438E-10], [4.977984552422638E7, 2.605528276282207E-10], [5.0224874338952586E7, 3.0472132316597615E-10], [5.066990315367879E7, 3.5554529731571266E-10], [5.111493196840499E7, 4.1374361499801304E-10], [5.15599607831312E7, 4.802117215806713E-10], [5.20049895978574E7, 5.558094077555362E-10], [5.2450018412583604E7, 6.417878759410009E-10], [5.289504722730981E7, 7.388241192935146E-10], [5.334007604203601E7, 8.483829617405332E-10], [5.3785104856762215E7, 9.715791090397122E-10], [5.423013367148842E7, 1.109785406559152E-9], [5.467516248621462E7, 1.2643900330719438E-9], [5.512019130094083E7, 1.4368108943687149E-9], [5.556522011566703E7, 1.628566343189144E-9], [5.6010248930393234E7, 1.841203725395433E-9], [5.645527774511944E7, 2.076330573006298E-9], [5.690030655984564E7, 2.335538522940906E-9], [5.7345335374571845E7, 2.62052904815362E-9], [5.779036418929805E7, 2.9327794695446844E-9], [5.823539300402425E7, 3.2740180112154628E-9], [5.8680421818750456E7, 3.6458103353868875E-9], [5.912545063347666E7, 4.0498091776117225E-9], [5.957047944820286E7, 4.487322520809177E-9], [6.001550826292907E7, 4.9597362092992626E-9], [6.046053707765527E7, 5.468303892841789E-9], [6.0905565892381474E7, 6.014213651906642E-9], [6.135059470710768E7, 6.5983691727551075E-9], [6.179562352183388E7, 7.2216208682147206E-9], [6.2240652336560085E7, 7.8844084686226E-9], [6.268568115128629E7, 8.587075959414329E-9], [6.28482233851995E7, 8.853693311282318E-9], [6.313070996601249E7, 9.32967474700829E-9], [6.35757387807387E7, 1.011199109260207E-8], [6.40207675954649E7, 1.09335065464597E-8], [6.4465796410191104E7, 1.1793449169777837E-8], [6.44755878360728E7, 1.181280801215937E-8], [6.491082522491731E7, 1.2690558859598791E-8], [6.535585403964351E7, 1.3623334453667256E-8], [6.56885366314496E7, 1.4342850205227887E-8], [6.5800882854369715E7, 1.4589942325286726E-8], [6.5954389359004E7, 1.4930805910037263E-8], [6.59957955491469E7, 1.5023375321376422E-8], [6.61306756043017E7, 1.5326757788147803E-8], [6.624591166909592E7, 1.5588124547199202E-8], [6.64987791275078E7, 1.6168367609449458E-8], [6.669094048382212E7, 1.6615289893310486E-8], [6.67985515651912E7, 1.6867688536811292E-8], [6.68286617704755E7, 1.6938578064858836E-8], [6.71257571793574E7, 1.76440754946745E-8], [6.713596929854833E7, 1.7668511494839517E-8], [6.758099811327453E7, 1.8744533331253364E-8], [6.802602692800073E7, 1.9839634065465218E-8], [6.847105574272694E7, 2.094994124042489E-8], [6.891608455745314E7, 2.207110660498474E-8], [6.936111337217934E7, 2.3198636683330236E-8], [6.980614218690555E7, 2.43278008987491E-8], [7.025117100163175E7, 2.54534931159742E-8], [7.069619981635796E7, 2.6570594541196303E-8], [7.114122863108416E7, 2.7673774202713362E-8], [7.158625744581036E7, 2.8757650746037896E-8], [7.203128626053657E7, 2.981671577818036E-8], [7.247631507526277E7, 3.0845499807369186E-8], [7.292134388998897E7, 3.183858532293057E-8], [7.336637270471518E7, 3.279067073631014E-8], [7.381140151944138E7, 3.3696587879127824E-8], [7.425643033416758E7, 3.455136678396202E-8], [7.470145914889379E7, 3.5350283838455187E-8], [7.514648796361999E7, 3.608890796416372E-8], [7.55915167783462E7, 3.6763144168252205E-8], [7.60365455930724E7, 3.736927384854325E-8], [7.64815744077986E7, 3.790399127624988E-8], [7.69266032225248E7, 3.836445478206697E-8], [7.737163203725101E7, 3.8748262142402406E-8], [7.781666085197721E7, 3.9053519251704975E-8], [7.826168966670342E7, 3.927884956996634E-8], [7.870671848142962E7, 3.94233696034651E-8], [7.915174729615583E7, 3.948678468780571E-8], [7.959677611088203E7, 3.946925409355749E-8], [8.004180492560823E7, 3.9371508744819396E-8], [8.048683374033444E7, 3.9194771495594455E-8], [8.093186255506064E7, 3.894075548973745E-8], [8.137689136978684E7, 3.8611641356875075E-8], [8.182192018451305E7, 3.821004966090461E-8], [8.226694899923925E7, 3.773900905564485E-8], [8.271197781396545E7, 3.72019206603661E-8], [8.315700662869166E7, 3.660251921590885E-8], [8.360203544341786E7, 3.594483161914214E-8], [8.404706425814407E7, 3.5233133459122756E-8], [8.449209307287027E7, 3.4471904192215197E-8], [8.493712188759647E7, 3.366578159567148E-8], [8.538215070232268E7, 3.281951612996849E-8], [8.582717951704888E7, 3.1937925820113106E-8], [8.627220833177508E7, 3.1025852235844646E-8], [8.671723714650129E7, 3.008811811115585E-8], [8.716226596122749E7, 2.9129487095894908E-8], [8.76072947759537E7, 2.8154646648937043E-8], [8.80523235906799E7, 2.7168096343443448E-8], [8.84973524054061E7, 2.617422513175658E-8], [8.89423812201323E7, 2.517721480439157E-8], [8.938741003485851E7, 2.41810320162934E-8], [8.983243884958471E7, 2.3189407825044138E-8], [9.027746766431092E7, 2.2205821074560293E-8], [9.072249647903712E7, 2.1233485605807686E-8], [9.116752529376332E7, 2.0275341216573355E-8], [9.161255410848953E7, 1.9334048237790056E-8], [9.205758292321573E7, 1.8411985545137688E-8], [9.250261173794194E7, 1.7511251782298922E-8], [9.294764055266814E7, 1.663366953680353E-8], [9.339266936739434E7, 1.5780775631084983E-8], [9.383769818212055E7, 1.495390000813015E-8], [9.428272699684675E7, 1.4154066502923548E-8], [9.472775581157295E7, 1.3382083975643697E-8], [9.517278462629916E7, 1.2638524471930467E-8], [9.561781344102536E7, 1.1923802890050125E-8], [9.606284225575157E7, 1.1238091169436712E-8], [9.650787107047777E7, 1.058135942881346E-8], [9.695289988520397E7, 9.953542283528161E-9], [9.739792869993018E7, 9.354338505184953E-9], [9.784295751465638E7, 8.783362592080174E-9], [9.828798632938258E7, 8.24003550863104E-9], [9.873301514410879E7, 7.723857607992927E-9], [9.917804395883499E7, 7.2340109090009805E-9], [9.96230727735612E7, 6.769847129123581E-9], [1.000681015882874E8, 6.3306097284233025E-9], [1.005131304030136E8, 5.915305124351162E-9], [1.009581592177398E8, 5.523243175234369E-9], [1.0140318803246601E8, 5.1532434870656E-9], [1.0184821684719221E8, 4.804745984312965E-9], [1.01977222475929E8, 4.707525435105867E-9], [1.02089234533847E8, 4.624563701027771E-9], [1.0229324566191842E8, 4.476712410095654E-9], [1.0231133038241E8, 4.463813543668641E-9], [1.02721221837595E8, 4.179772107087496E-9], [1.0273827447664462E8, 4.1683134742166996E-9], [1.0318330329137082E8, 3.8785311336003145E-9], [1.0362833210609703E8, 3.6065154295205816E-9], [1.0407336092082323E8, 3.351481046590766E-9], [1.0451838973554944E8, 3.112539078453586E-9], [1.0496341855027564E8, 2.8888572688053993E-9], [1.0540844736500184E8, 2.679547430495298E-9], [1.0585347617972805E8, 2.4840402979574927E-9], [1.0629850499445425E8, 2.3012326831355425E-9], [1.0674353380918045E8, 2.130819442111719E-9], [1.0718856262390666E8, 1.971847874486706E-9], [1.07203684407032E8, 1.9666218197075174E-9], [1.07625256255216E8, 1.8264027797279138E-9], [1.0763359143863286E8, 1.8237337777119162E-9], [1.0807862025335906E8, 1.6857392450770464E-9], [1.08198454550561E8, 1.6502593271720574E-9], [1.08281651662348E8, 1.6260345547402658E-9], [1.0852364906808527E8, 1.5574909912945333E-9], [1.0896867788281147E8, 1.4382573054610126E-9], [1.09311666571452E8, 1.3521022482256604E-9], [1.0941370669753768E8, 1.3274024860153028E-9], [1.0985873551226388E8, 1.2246399989874127E-9], [1.1030376432699008E8, 1.1292270218403532E-9], [1.1074879314171629E8, 1.0408433269220387E-9], [1.1119382195644249E8, 9.589848960330697E-10], [1.116388507711687E8, 8.832713306147079E-10], [1.120838795858949E8, 8.130325493467526E-10], [1.125289084006211E8, 7.480724922689638E-10], [1.129739372153473E8, 6.881226651176373E-10], [1.1341896603007351E8, 6.327293066870587E-10], [1.1386399484479971E8, 5.815692002512877E-10], [1.1430902365952592E8, 5.344638785445803E-10], [1.1475405247425212E8, 4.91069150382598E-10], [1.1519908128897832E8, 4.509614039719782E-10], [1.1564411010370453E8, 4.140779152625255E-10], [1.1608913891843073E8, 3.8006308543449447E-10], [1.1653416773315693E8, 3.4871182470643504E-10], [1.1697919654788314E8, 3.1995503714374155E-10], [1.1742422536260934E8, 2.933358233419446E-10], [1.17736868729233E8, 2.7599729971940436E-10], [1.1786925417733555E8, 2.68885439671462E-10], [1.1831428299206175E8, 2.4648701040861064E-10], [1.1875931180678795E8, 2.258484348284536E-10], [1.1920434062151416E8, 2.0685226101989284E-10], [1.1964936943624036E8, 1.8940166172436158E-10], [1.2009439825096656E8, 1.7332065120382097E-10], [1.2053942706569277E8, 1.5849029888121214E-10], [1.2098445588041897E8, 1.4499412939276048E-10], [1.2142948469514517E8, 1.3253328304050835E-10], [1.2187451350987138E8, 1.210495039456081E-10], [1.2231954232459758E8, 1.10517969058284E-10], [1.2276457113932379E8, 1.007160566626619E-10], [1.2320959995404999E8, 9.176892061718613E-11], [1.236546287687762E8, 8.358972192724367E-11], [1.240996575835024E8, 7.610055775138837E-11], [1.245446863982286E8, 6.917624216515354E-11], [1.249897152129548E8, 6.279238946779554E-11], [1.2543474402768101E8, 5.690396954524441E-11], [1.2587977284240721E8, 5.152993222344478E-11], [1.2632480165713342E8, 4.6613780453726226E-11], [1.2676983047185962E8, 4.208816714311242E-11], [1.2721485928658582E8, 3.7975002799880703E-11], [1.2765988810131203E8, 3.420747362655293E-11], [1.2810491691603823E8, 3.074362706735345E-11], [1.2854994573076443E8, 2.757960674712531E-11], [1.2899497454549064E8, 2.4725715207440208E-11], [1.2944000336021684E8, 2.209239863796729E-11], [1.2988503217494304E8, 1.9743084621891566E-11], [1.3033006098966925E8, 1.7587575465140008E-11], [1.3077508980439545E8, 1.5618135353233932E-11], [1.3122011861912166E8, 1.3868094610689186E-11], [1.3166514743384786E8, 1.2251758835808585E-11], [1.3211017624857406E8, 1.0812899075458919E-11], [1.3255520506330027E8, 9.485585240086989E-12], [1.3300023387802647E8, 8.332140175148446E-12], [1.3344526269275267E8, 7.299078382853736E-12], [1.3389029150747888E8, 6.391055383197989E-12], [1.4055760399782735E8, 5.828470932679188E-13]]
            List xyPairs1 = []
            List xyPairs2 = []
            Map seriesMap = [:]
            seriesMap["field1"] = xyPairs
            return ReportUtils.getRendererDataSource("pdfChart", JasperChartUtils.generatePDFChart(seriesMap, ["field1": SeriesColor.seriesColorList[0]]))

        }

        factory.metaClass.getPeriodCount = {->
            1
        }

        factory.metaClass.getFieldFunctionValues = {List<String> path, int periodIndex ->
            Collection currentValues = new ArrayList()
            currentValues << ["functionName": "ultimate, Gross", "meanValue": "1234567890.5", "varValue": "1234567890.5", "tVarValue": "1234567890.0"]
            currentValues << ["functionName": "paid", "meanValue": "6.5", "varValue": "8.5", "tVarValue": "5.0"]
            currentValues << ["functionName": "reserved", "meanValue": "1.5", "varValue": "7.5", "tVarValue": "65.0"]
            currentValues << ["functionName": "reserved", "meanValue": "1.5", "varValue": "7.5", "tVarValue": "65.0"]
            currentValues << ["functionName": "reserved", "meanValue": "1.5", "varValue": "7.5", "tVarValue": "65.0"]
            currentValues << ["functionName": "reserved", "meanValue": "1.5", "varValue": "7.5", "tVarValue": "65.0"]
            currentValues << ["functionName": "reserved", "meanValue": "1.5", "varValue": "7.5", "tVarValue": "65.0"]
            currentValues << ["functionName": "reserved", "meanValue": "1.5", "varValue": "7.5", "tVarValue": "65.0"]
            currentValues << ["functionName": "reserved", "meanValue": "1.5", "varValue": "7.5", "tVarValue": "65.0"]
            JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
            return jrBeanCollectionDataSource
        }


        factory.metaClass.getPeriodLabel = { int p ->
            return p + ""
        }
        model.factory = factory

        action.model = model
        model.metaClass.getReport = {->
            Map params = model.parameters
            JRBeanCollectionDataSource chartsDataSource = model.getCollectionDataSource()
            params["charts"] = chartsDataSource
            params["title"] = "Gira Report"
            params["footer"] = "sample report generated by PillarOne by testUser,"
            params["infos"] = createItemSettingsDataSource()
            params["currentUser"] = "testUser"
            params["itemInfo"] = "Gira report"
            params["_file"] = "GiraReport"
            params["SUBREPORT_DIR"] = ReportHelper.getReportFolder()
            params["Comment"] = "Comment"
            params["p1Icon"] = getClass().getResource(UIUtils.ICON_DIRECTORY + "application.png")
            params["p1Logo"] = getClass().getResource(UIUtils.ICON_DIRECTORY + "pillarone-logo-transparent-background-report.png")
            params["pdf_point"] = getClass().getResource(UIUtils.ICON_DIRECTORY + "pdf_point.png")
            return ReportHelper.getReportOutputStream(params, chartsDataSource).toByteArray()
        }
//in crise doesn't work
//                action.doActionPerformed(null)
        //                verifyExport(testExportFile)
    }


}
