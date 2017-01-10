<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * PageFetcherLib.php
 *
 * A custom library to fetch the web
 *
 * @author     Etienne THIERY
 * @version    0.0
 */

class PageFetcherLib {

	/**
	  * Constructor
      *
      */
	public function __construct() {
		$CI =& get_instance();
	}

	/**
     * Returns the rendered HTML code of the page or false on error
     *
     * @param String
     * @return String | Bool
     */
    public function fetch($url)
    {
		$url = $this->urlPostEncode($url);

        $os = php_uname('s');
        if ($os === 'Linux') {
            $phantom = '/pagefetcher/phantomJS/bin/phantomjs';
        } elseif ($os === 'Darwin') {
            $phantom = '/pagefetcher/phantomJS_macosx/bin/phantomjs';
        } else {
            return 'unsupported OS';
        }

        exec(__DIR__ . $phantom . ' ' . __DIR__ . '/pagefetcher/renderPage.js ' . ' ' . $url, $output);

		if (!isset($output[0]) || $output[0] === 'Error') {
            return false;
        } else {
			return htmlspecialchars(implode(array_slice($output, 1)));
        }
    }

	/**
	 * Returns an encoded URL, without encoding reserved characters
	 *
	 * @param String $url
	 * @return String
	 */
	private function urlPostEncode($url)
	{
		$url = explode('/', $url);
		$base = array_pop($url);

		return implode('/', $url) . '/' . urlencode($base);
	}
}

/* End of class PageFetcherLib.php */
/* Location: ./application/libraries/PageFetcherLib.php */
