<html>
<head>
  <title>PillarOne RiskAnalytics Administration Interface</title>
  <meta name="layout" content="main"/>
</head>
<body>
<h1 style="margin-left:20px;">Welcome to the PillarOne.RiskAnalytics Sandbox</h1>

<p style="margin-left:20px;width:80%;margin-bottom:10px">Start the application as</p>

<!--<div class="dialog" style="margin-left:20px;width:60%;">
    <ul>
        <g:each var="c" in="${grailsApplication.controllerClasses}">
  <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
</g:each>
    </ul>
</div>-->

<div class="dialog" style="margin-left:20px;width:60%;">
  <ul>
    <li><a href="application.gsp">in your browser (as an applet)</a></li>
    <li><a href="webstart.jsp">like a desktop application (using webstart)</a><br></li>
  </ul>

</div>
<div class="dialog" style="margin-left:20px;width:60%;margin-top:20px">
  Additional result view on the database:<br/>
  <p style="margin-left:20px;width:80%;margin-bottom:10px">
  <ul><li><g:link url="plugins/ulc-${new UlcGrailsPlugin().version}/application.gsp">General ULC view on domain classes</g:link></li></ul><br/><br/>
  <b>Warning</b>: Changes to the data sets are flushed once a day on the test server. </p>
</div>
</body>
</html>