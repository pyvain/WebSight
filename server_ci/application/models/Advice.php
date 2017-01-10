<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Advice extends MY_Model {

    function __construct()
    {
        parent::__construct();
        $this->load->database();
    }

    /**
	* Tells if $hostname corresponds to an existing advice
    *
    * @param string $hostname
    *
    * @return true if advice exists or false otherwise
    */
	public function adviceExists($hostname) {
		$sql = 'SELECT * FROM advice WHERE hostname = ?';
		$query = $this->db->query($sql, array($hostname)); // the parameters are automaticaly escaped
        return $query->num_rows() > 0;
	}

    /**
	* Returns the advice corresponding to $hostname or null if there
    * is no such advice.
    *
    * @param string $hostname
    *
    * @return an advice objetct or null
    */
	public function getAdviceByHostname($hostname) {
		$sql = 'SELECT * FROM advice WHERE hostname = ?';
		$query = $this->db->query($sql, array($hostname)); // the parameters are automaticaly escaped
        return $query->row();
	}

    /**
    * Returns advice that are inactive
    *
    * @return an array of advice objects or null
    */
    public function getInactiveAdvice() {
        $sql = 'SELECT * FROM advice WHERE active = (?)';
        $query = $this->db->query($sql, array(0)); // the parameters are automaticaly escaped
        if($this->db->affected_rows() == 0)
            return null;
        return $query->result();
    }

    /**
    * Returns advice that are active
    *
    * @return an array of advice objects or null
    */
    public function getActiveAdvice() {
        $sql = 'SELECT * FROM advice WHERE active = (?)';
        $query = $this->db->query($sql, array(1)); // the parameters are automaticaly escaped
        if($this->db->affected_rows() == 0)
            return null;
        return $query->result();
    }

    /**
	* Inserts a new empty advice in database
    *
    * @param string $hostname
    *
    * @return bool true if success, false if failure
    */
	public function addAdvice($hostname) {

        if($this->adviceExists($hostname))
            return false;

		$sql = 'INSERT INTO advice(hostname, active) VALUES (?, ?)';
        $query = $this->db->query($sql, array($hostname, 0));
        if($this->db->affected_rows() != 1)
            return false;
        return true;
	}

    /**
    * Updates the account url as $url in the advice identified by $hostname
    *
    * @param string $url
    * @param string $hostname
    *
    * @return bool true if success, false if failure
    */
    public function addAccountUrl($url, $hostname) {

        if(!$this->adviceExists($hostname))
            return false;

        $sql = 'UPDATE advice SET account_url=? WHERE hostname=?';
        $query = $this->db->query($sql, array($url, $hostname));
        if($this->db->affected_rows() != 1)
            return false;
        return true;
    }

    /**
    * Updates the delete url as $url in the advice identified by $hostname
    *
    * @param string $url
    * @param string $hostname
    *
    * @return bool true if success, false if failure
    */
    public function addDeleteUrl($url, $hostname) {

        if(!$this->adviceExists($hostname))
            return false;

        $sql = 'UPDATE advice SET delete_url=? WHERE hostname=?';
        $query = $this->db->query($sql, array($url, $hostname));
        if($this->db->affected_rows() != 1)
            return false;
        return true;
    }

    /**
    * Updates the report url as $url in the advice identified by $hostname
    *
    * @param string $url
    * @param string $hostname
    *
    * @return bool true if success, false if failure
    */
    public function addReportUrl($url, $hostname) {

        if(!$this->adviceExists($hostname))
            return false;

        $sql = 'UPDATE advice SET report_url=? WHERE hostname=?';
        $query = $this->db->query($sql, array($url, $hostname));
        if($this->db->affected_rows() != 1)
            return false;
        return true;
    }

    /**
    * Updates the contact email as $email in the advice identified by $hostname
    *
    * @param string $email
    * @param string $hostname
    *
    * @return bool true if success, false if failure
    */
    public function addContactEmail($email, $hostname) {

        if(!$this->adviceExists($hostname))
            return false;

        $sql = 'UPDATE advice SET contact_email=? WHERE hostname=?';
        $query = $this->db->query($sql, array($email, $hostname));
        if($this->db->affected_rows() != 1)
            return false;
        return true;
    }

    /**
    * Updates the global help as $help in the advice identified by $hostname
    *
    * @param string $help
    * @param string $hostname
    *
    * @return bool true if success, false if failure
    */
    public function addGlobalHelp($help, $hostname) {

        if(!$this->adviceExists($hostname))
            return false;

        $sql = 'UPDATE advice SET global_help=? WHERE hostname=?';
        $query = $this->db->query($sql, array($help, $hostname));
        if($this->db->affected_rows() != 1)
            return false;
        return true;
    }

    /**
    * Validates the advice so it can be use in the app
    *
    * @param string $hostname
    *
    * @return bool true if success, false if failure
    */
    public function validateAdvice($hostname) {

        if(!$this->adviceExists($hostname))
            return false;

        $sql = 'UPDATE advice SET active=? WHERE hostname=?';
        $query = $this->db->query($sql, array(1, $hostname));
        if($this->db->affected_rows() != 1)
            return false;
        return true;
    }

    /**
    * Validates the advice so it can be use in the app
    *
    * @param string $hostname
    *
    * @return bool true if success, false if failure
    */
    public function isActiveAdvice($hostname) {

        if(!$this->adviceExists($hostname))
            return false;

        $sql = 'SELECT * FROM advice WHERE hostname=?';
        $query = $this->db->query($sql, array($hostname)); // the parameters are automaticaly escaped
        $advice = $query->row();
        if (is_null($advice) || !$advice->active) {
            return false;
        }
        return true;
    }

    /**
    * Deletes advice in database
    *
    * @param int $hostname
    *
    * @return bool true if success, false if failure
    */
    public function deleteAdvice($hostname) {
        $sql = 'DELETE FROM advice WHERE hostname=?';
        $query = $this->db->query($sql, array($hostname));
        if($this->db->affected_rows() != 1)
            return false;
        return true;
    }

}
