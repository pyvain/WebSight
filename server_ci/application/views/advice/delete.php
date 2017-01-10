<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?>

<p>
    You still have the right to ask
    <?php if ($contact_email !== null) {
        echo "<a href=" ."mailto:" . $contact_email . ">the owner of this webpage</a>";
    } else {
        echo "the owner of this webpage";
    }?> to delete this data.
    Here is information about <a href="https://www.cnil.fr/fr/retrouver-les-coordonnees-dun-responsable-de-site">how to find a webpage's owner</a>.
</p>
