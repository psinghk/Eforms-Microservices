-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 27, 2021 at 12:02 PM
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
-- Database: `daonboarding`
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
-- Table structure for table `daonboarding_registration`
--

CREATE TABLE `daonboarding_registration` (
  `id` int(11) NOT NULL,
  `registration_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `user_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `pname` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `pdesignation` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `pemail` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `pmobile` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `paddress` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `uploaded_filename` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `renamed_filepath` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `emp_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `auth_off_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `designation` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `address` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `city` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `add_state` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `pin` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `ophone` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `rphone` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `auth_email` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `userip` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `datetime` timestamp NULL DEFAULT current_timestamp(),
  `support_action_taken` char(1) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT 'p' COMMENT 'p - processing, c - completed',
  `hod_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `hod_email` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `hod_mobile` char(15) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `hod_telephone` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `ca_desig` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `employment` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `ministry` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `department` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `other_dept` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `state` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `organization` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `pdf_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `sign_cert` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `rename_sign_cert` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `ca_sign_cert` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `ca_rename_sign_cert` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `renew_flag` tinyint(1) DEFAULT 0,
  `vpn_reg_no` varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `remarks` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `coordinator_email` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `bo_name` varchar(100) NOT NULL,
  `mobile_cofirmation` varchar(100) DEFAULT NULL,
  `eligibility` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `daonboarding_registration`
--

INSERT INTO `daonboarding_registration` (`id`, `registration_no`, `user_type`, `pname`, `pdesignation`, `pemail`, `pmobile`, `paddress`, `uploaded_filename`, `renamed_filepath`, `emp_code`, `auth_off_name`, `designation`, `address`, `city`, `add_state`, `pin`, `ophone`, `rphone`, `mobile`, `auth_email`, `userip`, `datetime`, `support_action_taken`, `hod_name`, `hod_email`, `hod_mobile`, `hod_telephone`, `ca_desig`, `employment`, `ministry`, `department`, `other_dept`, `state`, `organization`, `pdf_path`, `sign_cert`, `rename_sign_cert`, `ca_sign_cert`, `ca_rename_sign_cert`, `renew_flag`, `vpn_reg_no`, `remarks`, `coordinator_email`, `bo_name`, `mobile_cofirmation`, `eligibility`) VALUES
(1, 'DAONBOARDING-FORM202107130001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-13 10:51:49', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'F://pdfSaveFilesDAONBOARDING-FORM202107130001_1626173559237.pdf', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(2, 'DAONBOARDING-FORM202107130002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-13 10:57:22', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'F://pdfSaveFilesDAONBOARDING-FORM202107130002_1626173906839.pdf', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(3, 'DAONBOARDING-FORM202107140001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-14 07:16:44', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(4, 'DAONBOARDING-FORM202107140002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '127.0.0.1', '2021-07-14 11:36:08', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(5, 'DAONBOARDING-FORM202107150001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-15 04:13:51', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(6, 'DAONBOARDING-FORM202107150002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-15 04:15:28', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(7, 'DAONBOARDING-FORM202107150003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-15 04:17:44', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(8, 'DAONBOARDING-FORM202107150004', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-15 04:26:20', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(9, 'DAONBOARDING-FORM202107150005', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-15 04:27:13', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(10, 'DAONBOARDING-FORM202107150006', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-15 04:28:15', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(11, 'DAONBOARDING-FORM202107150007', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-15 04:32:20', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(12, 'DAONBOARDING-FORM202107150008', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-15 04:33:27', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(13, 'DAONBOARDING-FORM202107150009', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-15 04:34:21', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'false', 'emp_regular'),
(14, 'DAONBOARDING-FORM202107160001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-16 04:53:38', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(15, 'DAONBOARDING-FORM202107160002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '127.0.0.1', '2021-07-16 05:48:10', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(16, 'DAONBOARDING-FORM202107160003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-16 09:29:00', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(17, 'DAONBOARDING-FORM202107190001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-19 07:06:10', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'F://pdfSaveFilesDAONBOARDING-FORM202107190001_1626678437010.pdf', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(18, 'DAONBOARDING-FORM202107190002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-19 10:54:33', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(19, 'DAONBOARDING-FORM202107190003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-19 11:12:04', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(20, 'DAONBOARDING-FORM202107190004', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-19 11:13:07', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(21, 'DAONBOARDING-FORM202107190005', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-19 11:24:16', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(22, 'DAONBOARDING-FORM202107200001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 06:26:11', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(23, 'DAONBOARDING-FORM202107200002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 06:29:30', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(24, 'DAONBOARDING-FORM202107200003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 06:33:44', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(25, 'DAONBOARDING-FORM202107200004', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 07:40:22', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centtre Services Incorporated (NICSI)', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(26, 'DAONBOARDING-FORM202107200005', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 07:42:17', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(27, 'DAONBOARDING-FORM202107200006', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 07:59:54', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(28, 'DAONBOARDING-FORM202107200007', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 08:53:43', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'psu'),
(29, 'DAONBOARDING-FORM202107200008', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 09:07:00', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(30, 'DAONBOARDING-FORM202107200009', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-20 09:47:20', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(31, 'DAONBOARDING-FORM202107260001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-26 11:31:46', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(32, 'DAONBOARDING-FORM202107260002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-26 11:32:34', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'online', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(33, 'DAONBOARDING-FORM202107260003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-26 11:40:57', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular'),
(34, 'DAONBOARDING-FORM202107260004', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1235', 'Ms Meenaxi Indolia', 'Soft Eng. Software dev', 'cgo complex new delhi', 'SouthEast Delhi', 'DELHI', '110053', NULL, NULL, '+919958910444', 'meenaxi.nhq@nic.in', '0:0:0:0:0:0:0:1', '2021-07-26 11:44:41', 'p', 'ashwini kumar tiwri', 'tiwari.ashwini@nic.in', '+919953126961', '011-24305839', 'Scientist-C', 'Central', 'Electronics and Information Technology', 'National Informatics Centre', NULL, '', '', 'manual_upload', NULL, NULL, NULL, NULL, NULL, 'VPN328069', NULL, 'tiwari.ashwini@nic.in', 'Nic-official-id', 'true', 'emp_regular');

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
-- Indexes for table `daonboarding_registration`
--
ALTER TABLE `daonboarding_registration`
  ADD PRIMARY KEY (`id`);

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
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `application_type`
--
ALTER TABLE `application_type`
  MODIFY `app_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=261634;

--
-- AUTO_INCREMENT for table `daonboarding_registration`
--
ALTER TABLE `daonboarding_registration`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT for table `doc_upload`
--
ALTER TABLE `doc_upload`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18550;

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
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
