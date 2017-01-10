<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class User extends MY_Model {

    function __construct()
    {
        parent::__construct();
        $this->load->database();
    }

    /**
	* Tells if $email is already used by a user
    *
    * @param string $email
    * @return true if user exists or false otherwise
    */
	public function userExists($email) {
		$sql = 'SELECT id FROM users WHERE email = ?';
		$query = $this->db->query($sql, array($email)); // the parameters are automaticaly escaped
        return $query->num_rows() > 0;
	}

    /**
	* Returns the user corresponding to the email $email or null if there
    * is no such user.
    *
    * @param string $email
    * @return a user objetct or null
    */
	public function getUserByEmail($email) {
		$sql = 'SELECT * FROM users WHERE email = ?';
		$query = $this->db->query($sql, array($email)); // the parameters are automaticaly escaped
        return $query->row();
	}

    /**
    * Returns the user corresponding to the id $id or null if there
    * is no such user.
    *
    * @param int $id
    * @return a user object or null
    */
    public function getUserById($id) {
        $sql = 'SELECT * FROM users WHERE id = ?';
        $query = $this->db->query($sql, array($id)); // the parameters are automaticaly escaped
        return $query->row();
    }

    /**
	* Inserts a new user in database
	* (storing a salted encrypted password and base64 encoded salt)
    *
    * @param string $email
    * @param string $password
    * @return bool true if success, false if failure
    */
	public function addUser($email, $password) {

        if($this->userExists($email))
            return false;

		$salt = substr(base64_encode(openssl_random_pseudo_bytes(64)), 0, 63);
		$encrypted_password = hash('sha256', $salt.$password);
		$activation_hash = md5(openssl_random_pseudo_bytes(64));

		$sql = 'INSERT INTO users(email, encrypted_password, salt, hash) VALUES (?, ?, ?, ?)';
        $query = $this->db->query($sql, array($email, $encrypted_password, $salt, $activation_hash));
        if($this->db->affected_rows() != 1)
            return false;
        return true;
	}

    /**
    * Deletes user in database
    *
    * @param int $id
    *
    * @return bool true if success, false if failure
    */
    public function deleteUser($id) {
        $sql = 'DELETE FROM users WHERE id = ?';
        $query = $this->db->query($sql, array($id));
        if($this->db->affected_rows() != 1)
            return false;
        return true;
    }

    /**
    * Returns validation link for user $email
    * Fail if the account does not exist
    *
    * @param string $email
    * @return string|bool validation link or false if the user does'nt exist
    */
    public function getValidationLink($email) {
        // Check if account exists
        $sql = 'SELECT hash FROM users WHERE email = ?';
        $query = $this->db->query($sql, array($email));
        if($query->num_rows() == 0)
            return false;

        // create link
        $this->load->helper('url');
        $link = base_url() . 'auth/validate?email=' . $email . '&hash=' . $query->row()->hash;

        return $link;
    }


    /**
    * Returns a new password reset link for user $email
    * Fail if the account does not exist
    *
    * @param string $email
    * @return string|bool password reset link or false if the user does'nt exist
    */
    public function getResetLink($email) {
        // Check if account exists
        $sql = 'SELECT hash FROM users WHERE email = ?';
        $query = $this->db->query($sql, array($email));
        if($query->num_rows() != 1)
            return false;

        // update hash to create a new link
        $reset_hash = md5(openssl_random_pseudo_bytes(64));
        $sql = 'UPDATE users SET hash = ? WHERE email = ?';
        $query = $this->db->query($sql, array($reset_hash, $email));
        if($this->db->affected_rows() != 1)
            return false;

        // create link
        $this->load->helper('url');
        $link = base_url() . 'auth/resetpassword?email=' . $email . '&hash=' . $reset_hash;

        return $link;
    }


    /**
    * Sets the password of user 'email' with current hash 'hash' to 'password'
    * Fail if the account does not exist or hash is incorrect
    *
    * @param string $email
    * @param string $hash
    * @param string $password
    * @return bool true on success, false on failure
    */
    public function resetPassword($email, $hash, $password) {
        // Check if account exists with such hash
        $sql = 'SELECT salt FROM users WHERE email = ? AND hash = ?';
        $query = $this->db->query($sql, array($email, $hash));
        if($query->num_rows() != 1)
            return false;

        $encrypted_password = hash('sha256', $query->row()->salt . $password);

        // invalidate hash and set new password. set active to true in case it was a lost validation email
        $sql = 'UPDATE users SET hash = 0, active = 1, encrypted_password = ? WHERE email = ? AND hash = ?';
        $query = $this->db->query($sql, array($encrypted_password, $email, $hash));
        if($this->db->affected_rows() != 1)
            return false;

        return true;
    }


    /**
    * Tells if the hash is a valid hash for user 'email'
    *
    * @param string $email
    * @param string $hash
    * @return bool true if the hash is valid, false otherwise
    */
    public function isValidHash($email, $hash) {
        if($hash == "0")
            return false;

        // Check if account exists with such hash
        $sql = 'SELECT id FROM users WHERE email = ? AND hash = ?';
        $query = $this->db->query($sql, array($email, $hash));
        if($query->num_rows() != 1)
            return false;
        return true;
    }


    /**
    * Validates account
    * Fail if the account does not exist or has already been activated
    * or the hash is not correct
    *
    * @param string $email
    * @param string $hash
    * @return bool true if success, false if failure
    */
    public function validateAccount($email, $hash) {
        // check if it is a valid hash
        if($hash == "0")
            return false;

        // Check if account exists and is not active
        $sql = 'SELECT hash, active FROM users WHERE email = ?';
        $query = $this->db->query($sql, array($email));
        $user = $query->row();
        if(is_null($user) || $user->active || $hash !== $user->hash)
            return false;

        // Validate account
        $sql = 'UPDATE users SET hash = 0, active = 1 WHERE email = ?';
        $query = $this->db->query($sql, array($email));
        if($this->db->affected_rows() != 1)
            return false;

        return true;
    }

    /**
    * Verify if $password is correct for account $email
    * Fail if the account does not exist or the password is not correct
    *
    * @param string email
    * @param string unencrypted password
    * @return true if success, false if failure
    */
    public function checkPassword($email, $password) {
        // Check if account exists, and is active
        $sql = 'SELECT encrypted_password, salt, active FROM users '
        .'WHERE email = ?';
        $query = $this->db->query($sql, array($email));
        $user = $query->row();
        if(is_null($user) || !$user->active)
            return false;

        // Check password validity
        $encryptedSubmittedPassword = hash('sha256', $user->salt.$password);
        if ($encryptedSubmittedPassword !== $user->encrypted_password)
            return false;

        return true;
    }

}
