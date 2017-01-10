<?php
$dir = dirname(__FILE__);
include_once($dir.'/../src/application/Graph.class.php');
include_once($dir.'/../src/application/PageAnalyzer.php');

// tests for graph constructor
$nb_keywords = 2;
for ($i=1 ; $i<$nb_keywords+1 ; $i++) {
    $keywords[] = 'keyword_' . $i;
}
$graph = new Graph($keywords);
$graph->print_graph();

// tests for graph update function
$nb_urls = 4;
for ($i=1 ; $i<$nb_urls+1 ; $i++) {
    $urls[] = 'url' . $i;
}
foreach($keywords as $keyword) {
    for ($i = 0 ; $i < rand(1, $nb_urls) ; $i++) {
        $rand = rand(0, $nb_urls-1);
        $links[$keyword][] = $urls[$rand];
    }
}
$graph->update('keyword_3', $links);
$graph->print_graph();

//tests for analyze function
print_r(analyze(file_get_contents($dir."/source.html"), [wiki, facebook, twitter, zuckerberg, hjurin]));

?>
