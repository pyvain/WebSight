<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>WebSight | Reset password</title>
    <!--<link rel="stylesheet" href="style.css">-->
    <!--<script src="script.js"></script>-->
</head>
<body>
    <h1>Lost passwords' office</h1>
    <p>This form let's you reset your WebSight account password.</p>
    <p>Type in a new password:</p>

    <form action="resetpassword?email=<?php echo $email; ?>&amp;hash=<?php echo $hash; ?>" method="post">
        <h5>Password</h5>
        <span style="color: red;"><?php echo form_error('password'); ?></span>
        <input type="password" name="password" value="" size="50" />
        <h5>Retype password</h5>
        <span style="color: red;"><?php echo form_error('password2'); ?></span>
        <input type="password" name="password2" value="" size="50" />
        <div><input type="submit" value="Send request" /></div>
    </form>
</body>
</html>
