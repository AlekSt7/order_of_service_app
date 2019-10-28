-- phpMyAdmin SQL Dump
-- version 4.6.6deb4
-- https://www.phpmyadmin.net/
--
-- Хост: localhost:3306
-- Время создания: Июн 13 2019 г., 16:31
-- Версия сервера: 10.1.26-MariaDB-0+deb9u1
-- Версия PHP: 7.0.27-0+deb9u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `dolphins_order_of_service`
--

-- --------------------------------------------------------

--
-- Структура таблицы `digital_signage`
--

CREATE TABLE `digital_signage` (
  `digital_signage_id` int(11) NOT NULL,
  `number_of_installed_objects` int(11) DEFAULT NULL,
  `number_of_screens_of_differend_size` int(11) DEFAULT NULL,
  `screen_size` text,
  `fastening_type` text,
  `number_of_screens_object_on` int(11) DEFAULT NULL,
  `touch_screen` tinyint(1) DEFAULT NULL,
  `posting` text,
  `4k_content` tinyint(1) DEFAULT NULL,
  `type_of_installation_project` text,
  `unique_content` tinyint(1) DEFAULT NULL,
  `comment` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `e_ocher`
--

CREATE TABLE `e_ocher` (
  `e_ocher_id` int(11) NOT NULL,
  `number_of_operators` int(11) NOT NULL,
  `number_of_terminals` int(11) NOT NULL,
  `number_of_boards` int(11) NOT NULL,
  `approximate_coast` int(11) NOT NULL,
  `comment` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `orders`
--

CREATE TABLE `orders` (
  `order_Id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `digital_signage_id` int(11) DEFAULT NULL,
  `e_ocher_id` int(11) DEFAULT NULL,
  `tesosq` text,
  `digital_consultant` text,
  `order_status` tinyint(1) DEFAULT NULL,
  `date` date NOT NULL,
  `date_of_adoption` date NOT NULL,
  `reason_for_rejection` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `tokens`
--

CREATE TABLE `tokens` (
  `user_id` int(11) NOT NULL,
  `token` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `tokens`
--

INSERT INTO `tokens` (`user_id`, `token`) VALUES
(18, '65c6a9bd7af35c23f23e5de3cff4e8270f08914b'),
(18, '9ccde66b3a6e632eaa4ed7795d216408cfc84617'),
(18, '248780d0930522a96a9778e082d8cf37518177b4'),
(18, '06be6405dbd2cf2f8ef2663b11934df96bbcb577'),
(18, '6524f3d137854433aa89ea4d9d2c8a8485275c2b');

-- --------------------------------------------------------

--
-- Структура таблицы `user`
--

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `name` text,
  `phone_number` bigint(11) DEFAULT NULL,
  `organization_name` text,
  `job` text,
  `access_right` tinyint(1) DEFAULT '0',
  `e_mail` text,
  `sms_code` int(11) DEFAULT NULL,
  `sms_mailing` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `user`
--

INSERT INTO `user` (`user_id`, `name`, `phone_number`, `organization_name`, `job`, `access_right`, `e_mail`, `sms_code`, `sms_mailing`) VALUES
(18, 'Стулин Александр Сергеевич', 79260798648, 'Dolphins-IT', '', 0, 'alek.st7@yandex.ru', NULL, 1);

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `digital_signage`
--
ALTER TABLE `digital_signage`
  ADD PRIMARY KEY (`digital_signage_id`),
  ADD UNIQUE KEY `digital_signage_id` (`digital_signage_id`);

--
-- Индексы таблицы `e_ocher`
--
ALTER TABLE `e_ocher`
  ADD PRIMARY KEY (`e_ocher_id`),
  ADD UNIQUE KEY `e_ocher_id` (`e_ocher_id`);

--
-- Индексы таблицы `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_Id`),
  ADD UNIQUE KEY `order_Id` (`order_Id`);

--
-- Индексы таблицы `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `digital_signage`
--
ALTER TABLE `digital_signage`
  MODIFY `digital_signage_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT для таблицы `e_ocher`
--
ALTER TABLE `e_ocher`
  MODIFY `e_ocher_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT для таблицы `orders`
--
ALTER TABLE `orders`
  MODIFY `order_Id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT для таблицы `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
