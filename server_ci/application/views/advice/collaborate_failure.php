<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!DOCTYPE html>
<html>
<head>
	<?php $this->load->helper('url'); ?>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<meta name="description" content="">
	<meta name="author" content="">

	<!-- Bootstrap core CSS -->
	<link rel="stylesheet" href=<?=base_url() . "resources/css/bootstrap.min.css"?>>
	<!-- Custom styles for this template -->
	<link rel="stylesheet" href=<?=base_url() . "resources/css/callout.css"?>>

	<title>Not again!</title>
</head>
<body>
	<div class="bs-callout bs-callout-warning col-md-6 center">
		<div class="box-header row">
			<h4 class="box-title">This website is already in database.</h4>
			<p>
				Make a new try with another website<br>
			</p>
		</div>
	</div>
</body>
</html>
