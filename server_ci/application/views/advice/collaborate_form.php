<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!doctype html>
<html lang="en">
<head>
	<?php $this->load->helper('url'); ?>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<meta name="description" content="">
	<meta name="author" content="">
	<title>WebSight | Collaborators' office</title>

	<!-- Bootstrap core CSS -->
	<link rel="stylesheet" href=<?=base_url() . "resources/css/bootstrap.min.css"?>>
	<!-- Custom styles for this template -->
	<link rel="stylesheet" href=<?=base_url() . "resources/css/collaborate.css"?>>
	<link rel="stylesheet" href=<?=base_url() . "resources/css/callout.css"?>>


</head>
<body>
	<div class="container">
		<form action="collaborate?hostname=<?=$hostname?>" method="post">
			<h2 class="form-signin-heading">Collaborators' office</h2>
			<p>Fill in this form, and the concerned website will have a proper advice on our database!</p>
			<p>This form is totally anonymous.</p>
			<?=validation_errors()?>

			<label for="account_url" class="sr-only">Sign in URL</label>
			<input type="url" id="account_url" name="account_url" class="form-control" placeholder="Sign in URL">

			<label for="delete_url" class="sr-only">URL to delete an account</label>
			<input type="url" id="delete_url" name="delete_url" class="form-control" placeholder="URL to delete an account">

			<label for="report_url" class="sr-only">URL to report a content</label>
			<input type="url" id="report_url" name="report_url" class="form-control" placeholder="URL to report a content">

			<label for="contact_email" class="sr-only">The website's owner contact email</label>
			<input type="email" id="contact_email" name="contact_email" class="form-control" placeholder="The website's owner contact email">

			<label for="global_help" class="sr-only">Global advice on how to protect from a data link in this website</label>
			<textarea name="global_help" id="global_help" class="form-control" rows="5" cols="20" maxlength="500" placeholder="Global advice on how to protect from a data link in this website"></textarea>

			<button class="btn btn-lg btn-primary btn-block" type="submit">Submit</button>
		</form>
	</div>
</body>
</html>
