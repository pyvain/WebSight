<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>WebSight | Lost password</title>
    <!--<link rel="stylesheet" href="style.css">-->
    <!--<script src="script.js"></script>-->
</head>
<body>
    <h1>Lost passwords' office</h1>
    <p>You have lost your password?</p>
    <p>Fill in this form, and we will send you a link to reset your password.</p>

    <form action="lostpassword" method="post">
        <h5>Email</h5>
        <span style="color: red;"><?php echo form_error('email'); ?></span>
        <input type="email" name="email" value="" size="50" />
        <p>This is the email you use to log into your WebSight account.</p>
        <div><input type="submit" value="Send request" /></div>
    </form>
</body>
</html>
