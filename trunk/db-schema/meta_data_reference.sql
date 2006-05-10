--
-- PostgreSQL database dump
--

-- Started on 2006-05-10 17:25:02 Westeuropäische Normalzeit

SET client_encoding = 'SQL_ASCII';
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- TOC entry 1626 (class 0 OID 143482)
-- Dependencies: 1294
-- Data for Name: meta_data_reference; Type: TABLE DATA; Schema: public; Owner: univis
--

INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2014, 'Unit', 'Unit', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2018, 'Faculties only', 'Faculties only', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2017, 'Departments only', 'Departments only', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2015, 'Post', 'Post', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2019, 'Staff', 'Staff', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2013, 'Adminstrative unit', 'Administrative unit', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2011, 'dim_tage', 'by day', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2016, 'dim_dienst', 'Service', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1014, 'dim_fakultaet', 'by faculty', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1017, 'dim_lehrstuhl', 'by chair', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1016, 'dim_institut', 'by institute', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1015, 'dim_abteilung', 'by department', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2000, 'cob_busa_cube

', 'Orders', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1000, 'sos_cube', 'Students', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1011, 'bluep_geschlecht', 'Gender', 'geschlecht');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1009, 'dim_subkontinent', 'by sub-continent', 'region');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1008, 'dim_kontinent', 'by continent', 'kontinent');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1010, 'dim_land', 'by country', 'land');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1006, 'dim_abschluss', 'by degree', 'abschluss');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1005, 'dim_abschlussarten', 'by degree type', 'abschlussart');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1001, 'bluep_fachsem', 'Term', 'fachsem');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1002, 'dim_fachsemestergruppen', 'by term group', 'fs');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1003, 'dim_fachsemester', 'by term', 'gruppe');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2005, 'bluep_mittelherk', 'Funds', 'mittelherk');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1018, 'bluep_semester', 'Period', 'semester');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1019, 'dim_academ_jahr', 'by year (academic)', 'jahr');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1020, 'dim_semester', 'by semester', 'semester');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1012, 'bluep_hzb', 'Eligibility', 'hzb');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1013, 'bluep_lehreinheiten', 'Teaching unit', 'org_einheit');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2001, 'bluep_kostenart', 'Cost categories', 'kostenart');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2002, 'dim_kostenkategorien', 'Category', 'kategorie');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2003, 'dim_kostenunterkategorien', 'Sub-category', 'unterkategorie');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2004, 'dim_kostenarten', 'Class', 'kostenart');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2006, 'bluep_proj_inst', 'Project', 'projnr');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2007, 'bluep_jahr', 'by year', 'jahr');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2020, 'bluep_zeit', 'Zeit', 'zeit');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2008, 'dim_halbjahre', 'by semi-annual', 'halbjahr');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2009, 'dim_quartale', 'by quarter', 'quartal');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2010, 'dim_monate', 'by month', 'month');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (2012, 'bluep_institution', 'Institution', 'institution');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1007, 'bluep_nation', 'Nationality', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1021, 'bluep_nation', NULL, 'nation');
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1004, 'bluep_abschluss', 'Degree', NULL);
INSERT INTO meta_data_reference (id, table_name, i18n_key, joinable) VALUES (1022, 'bluep_abschluss', NULL, 'abschluss');


-- Completed on 2006-05-10 17:25:02 Westeuropäische Normalzeit

--
-- PostgreSQL database dump complete
--

