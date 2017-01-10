/* This script takes a unique argument which must be a valid URL.
If it is not the case, it writes 'Error' on the standard output.
Else, it writes 'Success\n', followed by the rendered HTML code
of the page on the standard output.*/

var system = require('system');
var time, address;
// Load phantomJS' webpage module, and create an instance
var webPage = require('webpage');
var page = webPage.create();
// Sets time out to 3 s
page.settings.resourceTimeout = 3000;

if (system.args.length !== 2) {
  console.log('Usage: renderPage.js <an URL>');
  phantom.exit();
}

t = Date.now();
address = system.args[1];
page.open(address, function(status) {
  if (status !== 'success') {
    console.log('Error');
  } else {
    t = Date.now() - t;
    console.log('Success');
    console.log(page.frameContent);
    console.log('Loading took: ' + t + ' ms');
  }
  phantom.exit();
});
