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
        <li>
            <g:link controller="application" action="applet">in your browser (as an applet)</g:link>
        </li>
        <li>
            <g:link controller="application" action="webstart">like a desktop application (using webstart)</g:link>
        </li>
    </ul>
</div>
</body>
</html>