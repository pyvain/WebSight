-- MySQL dump 10.13  Distrib 5.5.47, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: websight
-- ------------------------------------------------------
-- Server version	5.5.47-0+deb8u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `advice`
--

DROP TABLE IF EXISTS `advice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `advice` (
  `hostname` varchar(72) NOT NULL,
  `global_help` mediumtext,
  `account_url` varchar(2000) DEFAULT NULL,
  `delete_url` varchar(2000) DEFAULT NULL,
  `report_url` varchar(2000) DEFAULT NULL,
  `contact_email` varchar(255) DEFAULT NULL,
  `creation_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`hostname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `advice`
--

LOCK TABLES `advice` WRITE;
/*!40000 ALTER TABLE `advice` DISABLE KEYS */;
INSERT INTO `advice` VALUES ('facebook.com','Use the provided links to report abusive content so I won\'t be displayed or will no longer be associated to you. You can also consider deactivating your account. Note that this will not delete your data from Facebook\'s server. Make a habit of reviewing posts about you and watch what you post yourself as nothing can be completely deleted.','https://www.facebook.com/','https://www.facebook.com/deactivate/','https://www.facebook.com/help/contact/274459462613911','bla@bla.com','2016-04-25 19:51:31',1),('linkedin.com','','https://www.linkedin.com/uas/login?session_redirect=https://www.linkedin.com/people/invite-accept?mboxid=I6123786726818279424_500&sharedKey=tWFrJAlO&trkEmail=eml-email_m2m_invite_single_01-hero-0-accept~cta-null-5slc92~imq4x2v9~g3&fr=false&midToken=AQHg9f3LM5e3sg&invitationId=6123786697525248000&trk=eml-email_m2m_invite_single_01-hero-0-accept~cta&fe=true','https://www.linkedin.com/help/linkedin/answer/63/closing-your-account?lang=en','https://www.linkedin.com/help/linkedin/answer/56325','','2016-04-25 20:10:07',1),('twitter.com','','https://twitter.com/login/error?username_or_email=bib&redirect_after_login=/','https://twitter.com/settings/account','https://support.twitter.com/articles/82753#specific-violations','','2016-04-25 12:07:42',1),('wikipedia.org','If you find incorrect information on wikipedia you can\'t report it but you can edit the page yourself to remove it from the displayed page. It can still be found in the change log. To learn how to edit a page please consult <a href=\"https://en.wikipedia.org/wiki/Help:Menu/Editing_Wikipedia\">this page</a>.','https://en.wikipedia.org/w/index.php?title=Special:UserLogin&returnto=Wikipédia:Accueil+principal','https://en.wikipedia.org/wiki/Wikipedia:FAQ#How_do_I_change_my_username.2Fdelete_my_account.3F','https://en.wikipedia.org/wiki/Wikipedia:Contact_us','info-en@wikimedia.org','2016-04-25 20:20:48',1),('youtube.com','','https://accounts.google.com/ServiceLogin?passive=true&service=youtube&continue=https://www.youtube.com/signin?next=/&feature=sign_in_button&action_handle_signin=true&hl=fr&app=desktop&hl=fr&uilel=3#identifier','https://support.google.com/accounts/answer/32046?hl=en','https://www.youtube.com/yt/policyandsafety/reporting.html','bla@bla.com','2016-04-25 20:03:52',1);
/*!40000 ALTER TABLE `advice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `encrypted_password` varchar(64) NOT NULL,
  `salt` varchar(64) NOT NULL,
  `hash` varchar(32) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '0',
  `creation_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (74,'test@test.com','981e652d0000c9660d255c5c0437fe8f4abf02792996ea6472f9aa5474a0e5c8','DZXo4uOrRMUcAdYbRZRy+TtUTH/c7O18PEAsVXFZQMCxxtk0bVCGbsevtSEGzpv','0',1,'2016-04-25 17:00:08');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-04-25 23:49:24
