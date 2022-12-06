-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 23, 2021 at 07:20 AM
-- Server version: 10.4.18-MariaDB
-- PHP Version: 7.3.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vpn_database`
--

-- --------------------------------------------------------

--
-- Table structure for table `application_type`
--

CREATE TABLE `application_type` (
  `app_id` int(11) NOT NULL,
  `app_form_type` char(20) NOT NULL,
  `app_reg_no` varchar(100) NOT NULL,
  `app_user_type` char(50) DEFAULT NULL COMMENT 'esigned, manual, online',
  `app_user_path` varchar(255) DEFAULT NULL COMMENT 'if app_user_type is esigned then path of the esigned file ',
  `app_ca_type` char(50) DEFAULT NULL COMMENT 'esigned, manual, online',
  `app_ca_path` varchar(255) DEFAULT NULL COMMENT 'if app_ca_type is esigned then path of the esigned file ',
  `app_createdon` timestamp NOT NULL DEFAULT current_timestamp(),
  `app_updatedon` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `doc_upload`
--

CREATE TABLE `doc_upload` (
  `id` int(11) NOT NULL,
  `registration_no` varchar(255) NOT NULL,
  `doc` varchar(255) DEFAULT NULL,
  `doc_path` varchar(255) DEFAULT NULL,
  `role` varchar(100) NOT NULL,
  `extension` varchar(255) DEFAULT NULL,
  `original_filename` varchar(255) DEFAULT NULL,
  `upload_time` timestamp NULL DEFAULT current_timestamp(),
  `status` varchar(4) DEFAULT 'a'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `doc_upload`
--

INSERT INTO `doc_upload` (`id`, `registration_no`, `doc`, `doc_path`, `role`, `extension`, `original_filename`, `upload_time`, `status`) VALUES
(18550, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618274338277.pdf', '/Users/ashwini/eforms', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 00:38:58', NULL),
(18551, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618274431783.pdf', '/Users/ashwini/eforms', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 00:40:31', NULL),
(18552, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618274467462.pdf', '/Users/ashwini/eforms', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 00:41:07', NULL),
(18553, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618274554847.pdf', '/Users/ashwini/eforms', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 00:42:34', NULL),
(18554, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618274666257.pdf', '/Users/ashwini/eforms', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 00:47:07', NULL),
(18555, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618274831890.pdf', '/Users/ashwini/eforms', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 00:47:11', NULL),
(18556, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618274931468.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 00:48:51', NULL),
(18557, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618275426936.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (2).pdf', '2021-04-13 00:57:06', NULL),
(18558, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618275490910.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 00:58:10', NULL),
(18559, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618275797084.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (2).pdf', '2021-04-13 01:03:17', NULL),
(18560, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618275863762.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-13 01:04:23', NULL),
(18561, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618275991085.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-13 01:06:31', NULL),
(18562, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618276049010.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-13 01:07:29', NULL),
(18563, 'VPN-FORM202104130001', 'VPN-FORM202104130001_1618276182337.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090002.pdf', '2021-04-13 01:09:42', NULL),
(18564, 'VPN-FORM202104140001', 'VPN-FORM202104140001_1618355853759.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-13 23:17:33', NULL),
(18565, 'VPN-FORM20210414000154454', 'VPN-FORM20210414000154454_1618355936085.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-13 23:18:56', NULL),
(18566, 'VPN-FORM20210414000154454', 'VPN-FORM20210414000154454_1618355994233.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-13 23:19:54', NULL),
(18567, 'VPN-FORM20210414000154454', 'VPN-FORM20210414000154454_1618356103696.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (2).pdf', '2021-04-13 23:21:45', NULL),
(18568, 'VPN-FORM20210414000154454', 'VPN-FORM20210414000154454_1618356137616.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 23:22:17', NULL),
(18569, 'VPN-FORM20210414000154454', 'VPN-FORM20210414000154454_1618356231262.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (2).pdf', '2021-04-13 23:23:51', NULL),
(18570, 'VPN-FORM202104140001', 'VPN-FORM202104140001_1618356328972.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 23:25:28', NULL),
(18571, 'VPN-FORM202104140001', 'VPN-FORM202104140001_1618357227178.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 23:40:27', NULL),
(18572, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618357379964.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-13 23:42:59', NULL),
(18573, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618357405130.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (2).pdf', '2021-04-13 23:43:25', NULL),
(18574, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618357912073.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-13 23:51:52', NULL),
(18575, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618358074146.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-13 23:54:34', NULL),
(18576, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618358108852.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090002.pdf', '2021-04-13 23:55:08', NULL),
(18577, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618358646277.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-14 00:04:06', NULL),
(18578, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618358696961.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090002.pdf', '2021-04-14 00:04:56', NULL),
(18579, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618358785412.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-14 00:06:25', NULL),
(18580, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618358886875.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-14 00:08:06', NULL),
(18581, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618358966259.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-14 00:09:26', NULL),
(18582, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359022042.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-14 00:10:22', NULL),
(18583, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359069677.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (2).pdf', '2021-04-14 00:11:09', NULL),
(18584, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359136552.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-14 00:12:16', NULL),
(18585, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359176727.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-14 00:12:56', NULL),
(18586, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359246289.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-14 00:14:06', NULL),
(18587, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359316160.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090002.pdf', '2021-04-14 00:15:16', NULL),
(18588, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359434357.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-14 00:17:14', NULL),
(18589, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359475284.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090002.pdf', '2021-04-14 00:17:55', NULL),
(18590, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359535604.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-14 00:18:55', NULL),
(18591, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359623031.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090002.pdf', '2021-04-14 00:20:23', NULL),
(18592, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359692470.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090002.pdf', '2021-04-14 00:21:32', NULL),
(18593, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359864745.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-14 00:24:24', NULL),
(18594, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618359945553.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-14 00:25:45', NULL),
(18595, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618360038447.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-14 00:27:18', NULL),
(18596, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618360229643.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-14 00:30:29', NULL),
(18597, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618360293433.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (2).pdf', '2021-04-14 00:31:33', NULL),
(18598, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618360363545.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-14 00:32:43', NULL),
(18599, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618360543368.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-14 00:35:43', NULL),
(18600, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618360610940.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (2).pdf', '2021-04-14 00:36:50', NULL),
(18601, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618360665374.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-14 00:37:45', NULL),
(18602, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618360756135.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (2).pdf', '2021-04-14 00:39:16', NULL),
(18603, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618360866556.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003.pdf', '2021-04-14 00:41:06', NULL),
(18604, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618361064686.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (2).pdf', '2021-04-14 00:44:24', NULL),
(18605, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618361114276.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'SMS-FORM202104090003 (1).pdf', '2021-04-14 00:45:14', NULL),
(18606, 'VPN-FORM202104140004', 'VPN-FORM202104140004_1618503938471.pdf', '/home/gaurav/Music/pdfpath', 'USER', 'pdf', 'WIFI-FORM202104080008.pdf', '2021-04-15 16:25:38', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `esign`
--

CREATE TABLE `esign` (
  `id` int(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `query_raise`
--

CREATE TABLE `query_raise` (
  `qr_id` int(11) NOT NULL,
  `qr_form_type` varchar(50) DEFAULT NULL,
  `qr_reg_no` varchar(100) NOT NULL,
  `qr_forwarded_by` char(2) DEFAULT NULL COMMENT 'ca - competent authority, s - support, c - coordinator, m - mail-admin, d- da-admin',
  `qr_forwarded_by_user` varchar(255) DEFAULT NULL,
  `qr_forwarded_to` char(2) DEFAULT NULL COMMENT 'ca - competent authority,s - support, c - coordinator, m - mail-admin',
  `qr_forwarded_to_user` text DEFAULT NULL,
  `qr_message` text DEFAULT NULL,
  `qr_createdon` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `remarks`
--

CREATE TABLE `remarks` (
  `id` int(11) NOT NULL,
  `role` varchar(2) NOT NULL,
  `remarks` varchar(45) DEFAULT NULL,
  `createdon` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `status`
--

CREATE TABLE `status` (
  `stat_id` int(11) NOT NULL,
  `stat_form_type` varchar(50) DEFAULT NULL,
  `stat_reg_no` varchar(100) NOT NULL,
  `stat_type` char(50) NOT NULL DEFAULT 'ca_pending' COMMENT 'ca_pending,ca_rejected,support_pending, support_rejected, coordinator_pending, coordinator_rejected, mail-admin_pending, mail-admin_rejected, completed,cancel',
  `stat_forwarded_by` char(2) DEFAULT NULL COMMENT 'ca - competent authority, s - support, c - coordinator, m - mail-admin, d- da-admin',
  `stat_forwarded_by_user` varchar(255) DEFAULT NULL,
  `stat_forwarded_to` char(2) DEFAULT NULL COMMENT 'ca - competent authority,s - support, c - coordinator, m - mail-admin',
  `stat_forwarded_to_user` text DEFAULT NULL,
  `stat_remarks` text DEFAULT NULL,
  `stat_ip` varchar(20) DEFAULT NULL,
  `stat_active` char(1) NOT NULL DEFAULT 'a' COMMENT 'y - yes, n - no',
  `stat_createdon` timestamp NOT NULL DEFAULT current_timestamp(),
  `stat_final_id` varchar(50) DEFAULT NULL,
  `stat_on_hold` char(1) NOT NULL DEFAULT 'n',
  `stat_process` varchar(100) DEFAULT NULL,
  `stat_forwarded_by_email` varchar(100) DEFAULT NULL,
  `stat_forwarded_by_mobile` varchar(100) DEFAULT NULL,
  `stat_forwarded_by_name` varchar(100) DEFAULT NULL,
  `stat_forwarded_by_ip` varchar(100) DEFAULT NULL,
  `stat_forwarded_by_datetime` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `vpn_entries`
--

CREATE TABLE `vpn_entries` (
  `id` int(11) NOT NULL,
  `registration_no` varchar(50) NOT NULL,
  `ip_type` varchar(10) DEFAULT NULL,
  `ip1` varchar(20) DEFAULT NULL,
  `ip2` varchar(20) DEFAULT NULL,
  `server_location` varchar(50) DEFAULT NULL,
  `server_loc_other` varchar(50) DEFAULT NULL,
  `app_url` varchar(100) DEFAULT NULL,
  `dest_port` varchar(50) DEFAULT NULL,
  `deleted_flag` char(1) DEFAULT 'N',
  `deleted_by` varchar(10) DEFAULT '',
  `datetime` datetime DEFAULT NULL,
  `action_type` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `vpn_entries`
--

INSERT INTO `vpn_entries` (`id`, `registration_no`, `ip_type`, `ip1`, `ip2`, `server_location`, `server_loc_other`, `app_url`, `dest_port`, `deleted_flag`, `deleted_by`, `datetime`, `action_type`) VALUES
(1, 'VPN-FORM202107190001', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, 'http://arbc.com', '60', NULL, NULL, NULL, 'Add'),
(2, 'VPN-FORM202107190001', 'single', '10.10.10.11', NULL, 'NDC Delhi', NULL, 'http://tabc.com', '50', NULL, NULL, NULL, 'Add'),
(3, 'VPN-FORM202107190001', 'single', '10.10.10.12', NULL, 'NDC Delhi', NULL, 'http://aobc.com', '30', NULL, NULL, NULL, 'Add'),
(4, 'VPN-FORM202107190001', 'single', '10.10.10.13', NULL, 'NDC Delhi', NULL, 'http://apbc.com', '45', NULL, NULL, NULL, 'Add'),
(5, 'VPN-FORM202107190002', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, 'http://abc.com', '89', NULL, NULL, NULL, 'Add'),
(6, 'VPN-FORM202107190002', 'single', '10.10.12.13', NULL, 'NDC Delhi', NULL, 'http://abc.com', '90', NULL, NULL, NULL, 'Add'),
(7, 'VPN-FORM202107200001', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, 'http://abc.com', '89', NULL, NULL, NULL, 'Add'),
(8, 'VPN-FORM202107200001', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, 'http://abc.com', '20', NULL, NULL, NULL, 'Add'),
(9, 'VPN-FORM202107200001', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, 'http://abc.com', '30', NULL, NULL, NULL, 'Add'),
(10, 'VPN-FORM202107200001', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, 'http://abc.com', '80', NULL, NULL, NULL, 'Add'),
(11, 'VPN-FORM202107200001', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, 'http://abc.com', '40', NULL, NULL, NULL, 'Add'),
(12, 'VPN-FORM202107200002', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, 'https://abc.com', '80', NULL, NULL, NULL, 'Add'),
(13, 'VPN-FORM202107200003', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, 'http://abc.com', '50', NULL, NULL, NULL, 'Add'),
(14, 'VPN-FORM202107200004', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, 'http://abc.com', '90', NULL, NULL, NULL, 'Add'),
(15, 'VPN-FORM202107200005', 'single', '10.10.10.10', NULL, 'NDC Delhi', NULL, '', '80', NULL, NULL, NULL, 'Add'),
(16, 'VPN-FORM202107200007', 'single', '10.10.10.10', NULL, NULL, NULL, 'http://abc.com', '90', NULL, NULL, NULL, 'Add'),
(17, 'VPN-FORM202107230001', 'single', '10.10.10.10', NULL, NULL, NULL, '', '90', NULL, NULL, NULL, 'Add');

-- --------------------------------------------------------

--
-- Table structure for table `vpn_registration`
--

CREATE TABLE `vpn_registration` (
  `id` int(11) NOT NULL,
  `registration_no` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `user_type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ip_type` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ip1` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ip2` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `server_location` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `server_loc_other` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `app_url` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dest_port` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pname` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pdesignation` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pemail` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pmobile` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `paddress` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `uploaded_filename` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `renamed_filepath` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `emp_code` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `auth_off_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `designation` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `city` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `add_state` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pin` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ophone` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `rphone` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `mobile` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `auth_email` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `userip` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `datetime` timestamp NULL DEFAULT current_timestamp(),
  `support_action_taken` char(1) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'p' COMMENT 'p - processing, c - completed',
  `hod_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `hod_email` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `hod_mobile` char(15) COLLATE utf8_unicode_ci NOT NULL,
  `hod_telephone` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `ca_desig` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `employment` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ministry` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `department` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `other_dept` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `state` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `organization` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `pdf_path` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sign_cert` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `rename_sign_cert` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ca_sign_cert` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ca_rename_sign_cert` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `renew_flag` tinyint(1) DEFAULT 0,
  `vpn_reg_no` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `remarks` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `coordinator_email` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `vpn_registration`
--

INSERT INTO `vpn_registration` (`id`, `registration_no`, `user_type`, `ip_type`, `ip1`, `ip2`, `server_location`, `server_loc_other`, `app_url`, `dest_port`, `pname`, `pdesignation`, `pemail`, `pmobile`, `paddress`, `uploaded_filename`, `renamed_filepath`, `emp_code`, `auth_off_name`, `designation`, `address`, `city`, `add_state`, `pin`, `ophone`, `rphone`, `mobile`, `auth_email`, `userip`, `datetime`, `support_action_taken`, `hod_name`, `hod_email`, `hod_mobile`, `hod_telephone`, `ca_desig`, `employment`, `ministry`, `department`, `other_dept`, `state`, `organization`, `pdf_path`, `sign_cert`, `rename_sign_cert`, `ca_sign_cert`, `ca_rename_sign_cert`, `renew_flag`, `vpn_reg_no`, `remarks`, `coordinator_email`) VALUES
(1, 'VPN-FORM202107190001', 'vpn_single', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-19 09:56:53', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', '/Users/ashwini/eformsVPN-FORM202107190001_1626696939606.pdf', NULL, NULL, NULL, NULL, NULL, NULL, 'issues', NULL),
(2, 'VPN-FORM202107190002', 'vpn_single', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-19 09:57:51', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, NULL, 'thrhh', NULL),
(3, 'VPN-FORM202107200001', 'vpn_single', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 05:14:15', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, NULL, 'issues', NULL),
(4, 'VPN-FORM202107200002', 'vpn_single', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 09:30:55', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, NULL, 'test', NULL),
(5, 'VPN-FORM202107200003', 'vpn_single', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 09:37:00', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'F:/pdfSaveFilesVPN-FORM202107200003_1626774721033.pdf', NULL, NULL, NULL, NULL, NULL, NULL, 'ffffffffff', NULL),
(6, 'VPN-FORM202107200004', 'vpn_single', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '6110', 'Ashwini Kumar Tiwari', 'Scientist-C', 'CGO COMPLEX', 'Central', 'DELHI', '110003', NULL, NULL, '+919953126961', 'ashwin@gov.in', '0:0:0:0:0:0:0:1', '2021-07-20 09:53:26', 'p', 'Seema khanna', 'seema@gov.in', '+919810158558', '011-24305367', 'Scientist-F', 'Central', 'Electronics and Information Technology', 'NIC Employees', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, NULL, 'frfrefr', NULL),
(7, 'VPN-FORM202107200005', 'vpn_single', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 10:25:24', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, NULL, 'gbbbbbb', NULL),
(8, 'VPN-FORM202107200006', 'vpn_single', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 11:06:45', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, NULL, 'vfvff', NULL),
(9, 'VPN-FORM202107200007', 'vpn_single', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 11:10:55', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, NULL, 'fvff', NULL),
(10, 'VPN-FORM202107230001', 'vpn_single', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-23 04:38:01', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, NULL, 'kjkjjk', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `application_type`
--
ALTER TABLE `application_type`
  ADD PRIMARY KEY (`app_id`),
  ADD KEY `app_reg_no_index` (`app_reg_no`);

--
-- Indexes for table `doc_upload`
--
ALTER TABLE `doc_upload`
  ADD PRIMARY KEY (`id`),
  ADD KEY `index_registration_no_role` (`registration_no`,`role`);

--
-- Indexes for table `query_raise`
--
ALTER TABLE `query_raise`
  ADD PRIMARY KEY (`qr_id`),
  ADD KEY `idx_qr_reg_no` (`qr_reg_no`),
  ADD KEY `qr_forwarded_by_user_index` (`qr_forwarded_by_user`),
  ADD KEY `qr_form_type_index` (`qr_form_type`),
  ADD KEY `index_reg_no_createdon` (`qr_reg_no`,`qr_createdon`),
  ADD KEY `index_forwarded_by_and_user_createdon` (`qr_forwarded_by`,`qr_forwarded_by_user`,`qr_createdon`),
  ADD KEY `index_forwarded_by_createdon` (`qr_forwarded_by`,`qr_createdon`),
  ADD KEY `index_id_to_createdon` (`qr_id`,`qr_forwarded_to`,`qr_createdon`),
  ADD KEY `index_qr_createdon` (`qr_createdon`);

--
-- Indexes for table `remarks`
--
ALTER TABLE `remarks`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `status`
--
ALTER TABLE `status`
  ADD PRIMARY KEY (`stat_id`),
  ADD KEY `stat_reg_no` (`stat_reg_no`),
  ADD KEY `stat_forwarded_by_email_index` (`stat_forwarded_by_email`),
  ADD KEY `stat_forwarded_by_mobile_index` (`stat_forwarded_by_mobile`),
  ADD KEY `stat_forwarded_by_name_index` (`stat_forwarded_by_name`),
  ADD KEY `stat_forwarded_by_ipindex` (`stat_forwarded_by_ip`),
  ADD KEY `stat_forwarded_by_datetime_index` (`stat_forwarded_by_datetime`),
  ADD KEY `stat_final_id_index` (`stat_final_id`),
  ADD KEY `index_FormtypeRegnoTypeByTo` (`stat_form_type`,`stat_reg_no`,`stat_type`,`stat_forwarded_by`,`stat_forwarded_to`),
  ADD KEY `index_RegnoToType` (`stat_reg_no`,`stat_forwarded_to`,`stat_type`),
  ADD KEY `index_RegnoBy` (`stat_reg_no`,`stat_forwarded_by`),
  ADD KEY `index_Type` (`stat_type`),
  ADD KEY `index_RegnoCreatedon` (`stat_reg_no`,`stat_createdon`),
  ADD KEY `master_regno_statid_index` (`stat_reg_no`,`stat_id`);

--
-- Indexes for table `vpn_entries`
--
ALTER TABLE `vpn_entries`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `vpn_registration`
--
ALTER TABLE `vpn_registration`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reg_idx` (`registration_no`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `application_type`
--
ALTER TABLE `application_type`
  MODIFY `app_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=261634;

--
-- AUTO_INCREMENT for table `doc_upload`
--
ALTER TABLE `doc_upload`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18607;

--
-- AUTO_INCREMENT for table `query_raise`
--
ALTER TABLE `query_raise`
  MODIFY `qr_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48334;

--
-- AUTO_INCREMENT for table `remarks`
--
ALTER TABLE `remarks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vpn_entries`
--
ALTER TABLE `vpn_entries`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `vpn_registration`
--
ALTER TABLE `vpn_registration`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
