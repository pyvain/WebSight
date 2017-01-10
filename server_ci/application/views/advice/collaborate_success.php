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

	<title>Thank your for your collaboration!</title>
</head>
<body>
	<div class="bs-callout bs-callout-primary col-md-6 center">
		<div class="box-header row">
			<h4 class="box-title">Your advice was successfully submitted.</h4>
			<p>
				Thank you for your collaboration!<br>
				A moderator will soon complete these information and make it an effective advice!
			</p>
		</div>
	</div>
</body>
</html>
