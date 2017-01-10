<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?>
<?php $this->load->helper('url'); ?>
	<p>
		There is no stored advice corresponding to this website.
		If you wish, you can <a href=<?=base_url() . "request/collaborate?hostname=" . $hostname?>>collaborate</a> and add relevant advice to our database!
	</p>
