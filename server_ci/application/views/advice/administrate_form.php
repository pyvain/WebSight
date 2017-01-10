<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!doctype html>
<html lang="en">
<head><?php $this->load->helper('url'); ?>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<meta name="description" content="">
	<meta name="author" content="">

	<!-- Bootstrap core CSS -->
	<link rel="stylesheet" href=<?=base_url() . "resources/css/bootstrap.min.css"?>>
	<!-- Custom styles for this template -->
	<link rel="stylesheet" href=<?=base_url() . "resources/css/collaborate.css"?>>
	<link rel="stylesheet" href=<?=base_url() . "resources/css/callout.css"?>>
	<!-- Bootstrap and jquery core js -->
	<script type="text/javascript" src=<?=base_url() . "resources/js/jquery-2.2.3.min.js"?>></script>
	<script type="text/javascript" src=<?=base_url() . "resources/js/bootstrap.min.js"?>></script>

	<title>WebSight | Administrators' office</title>

</head>
<body>
	<div class="container">

		<h1>Administrators' office</h1>
		<p>
			Here are all the advice of our database.
		</p>
		<p>
			You can edit or delete them.
		</p>
		<?=validation_errors()?>
		<?php foreach ($advice as $a): ?>
			<div class="container" style="margin-bot:10;">

				<?php $id=explode('.', $a->hostname)[0]; ?>
				<form action="administrate" method="post">
					<button class="btn btn-primary collapsed" type="button" style="width:30%; margin-bottom: 2px;" data-toggle="collapse" data-target=<?='#' . $id?> aria-expanded="false">
						<?=$a->hostname?>
					</button>
					<input type="hidden" name="hostname" value=<?=$a->hostname?>>
					<div class="collapse" id=<?=$id?> aria-expanded="false">
						<div class="well">
							<div class="input-group">
								<span class="input-group-addon" id="sizing-addon2">Sign in URL</span>
								<label for="account_url" class="sr-only">Sign in URL</label>
								<input type="url" id="account_url" name="account_url" class="form-control" placeholder="Sign in URL" value=<?=$a->account_url?>>
							</div>
							<div class="input-group">
								<span class="input-group-addon" id="sizing-addon2">Deletion URL</span>
								<label for="delete_url" class="sr-only">URL to delete an account</label>
								<input type="url" id="delete_url" name="delete_url" class="form-control"  placeholder="URL to delete an account" value=<?=$a->delete_url?>>
							</div>
							<div class="input-group">
								<span class="input-group-addon" id="sizing-addon2">Report URL</span>
								<label for="report_url" class="sr-only">URL to report a content</label>
								<input type="url" id="report_url" name="report_url" class="form-control" placeholder="URL to report a content" value=<?=$a->report_url?> >
							</div>
							<div class="input-group">
								<span class="input-group-addon" id="sizing-addon2">Contact Mail</span>
								<label for="contact_email" class="sr-only">The website's owner contact email</label>
								<input type="email" id="contact_email" name="contact_email" class="form-control" placeholder="The website's owner contact email" value=<?=$a->contact_email?> >
							</div>
							<div class="input-group">
								<span class="input-group-addon" id="sizing-addon2">Global Help</span>
								<label for="global_help" class="sr-only">Global help to preserve confidentiality</label>
								<textarea name="global_help" id="global_help" class="form-control" rows="5" cols="20" maxlength="500" placeholder="Global help to preserve confidentiality"><?=$a->global_help?></textarea>
							</div>

							<button type="submit" name="edit" class="btn btn-primary">Edit</button>
							<button type="submit" name="delete" class="btn btn-danger">Delete</button>
						</div>
					</div>
				</form>
			</div>
		<?php endforeach ?>
	</div>
</body>
</html>
