<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Shared Session Filter sample with Memcached</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="assets/css/bootstrap.css" rel="stylesheet">
    <style>
      body {
        padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
      }
    </style>
    <link href="assets/css/bootstrap-responsive.css" rel="stylesheet">

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Le fav and touch icons -->
    <link rel="shortcut icon" href="assets/ico/favicon.ico">
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="assets/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="assets/ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="assets/ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="assets/ico/apple-touch-icon-57-precomposed.png">
  </head>

  <body>

    <div class="container">

<h1>shared-session-filter with Memcached</h1>
<hr/>

<h3>#toString()</h3>
<hr/>
<p>
<%= session.toString() %><br/>
</p>

<hr/>
<h3>Session</h3>
<hr/>
<p>
<table class="table table-bordered">
  <thead>
    <tr>
      <th>getCreationTime</th>
      <th>getLastAcccessedTime</th>
      <th>getMaxInactiveInterval</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><%= new java.util.Date(session.getCreationTime()).toString() %></td>
      <td><%= new java.util.Date(session.getLastAccessedTime()).toString() %></td>
      <td><%= session.getMaxInactiveInterval() %></td>
    </tr>
  </tbody>
</table>
<p>

<hr/>
<h3>Operations</h3>
<hr/>
<p>
<%
Integer counter = (Integer) session.getAttribute("counter");
%>
Get: counter -> <%= counter %><br/>
counter++<br/>
<%
if (counter == null) counter = 0;
counter++;
session.setAttribute("counter", Integer.valueOf(counter));
%>
Set: counter -> <%= counter %><br/>
<br/>
</p>

<%
if (counter > 5) {
  session.invalidate();
%>
<div class="alert">
Session is invalidated!
</div>
<%
}
%>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="assets/js/jquery.min.js"></script>
    <script src="assets/js/bootstrap.min.js"></script>

  </body>
</html>

