<?php

ini_set('error_reporting', E_ALL); //Натройка для вывода ошибок в php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);

	/*ГЛОБАЛНЫЕ ПЕРЕМЕННЫЕ*/

	$connect = mysqli_connect('localhost', 'db', '12019991', 'dolphins_order_of_service') or die(); //Подключение к базе данных
	$sms_api_key = "21FD42DE-F13C-E10E-3EF9-122832D1AE8A&to"; //APi Key для отправки sms

	/**********************/

	/*БЛОК ВЫЗОВОВ ФУНКЦИЙ*/
	if(isset($_POST['api_method'])){
	if ($_POST['api_method'] == "sms_start") {sms_code($connect, $_POST['phone_number'], $sms_api_key);} //Функция генерации sms кода и отпрпавки его по sms
	else if ($_POST['api_method'] == "auth_start") {auth($connect, $_POST['sms_code'], $_POST['phone_number']);} //Функция генерации sms кода и отпрпавки его по sms
	else if ($_POST['api_method'] == "session_exist") {session_exist($connect, $_POST['token']);} //Проверяет наличие определённого токена
	else if ($_POST['api_method'] == "session_unset") {sess_unset($connect, $_POST['token']);} //Удаляет сессию (токен)
	else if ($_POST['api_method'] == "get_user_data") {get_user_data($connect, $_POST['token']);} //Получает пользовательские данные
	else if ($_POST['api_method'] == "user_data_update") {user_data_update($connect, $_POST['token'], $_POST['name'], $_POST['phone_number'], $_POST['organization_name'], $_POST['job'], $_POST['e_mail']);} //Обновляет пользовательские данные
	else if ($_POST['api_method'] == "sms_mailing") {sms_mailing($connect, $_POST['token'], $_POST['mailing']);} 
	}
	/**********************/

	/*БЛОК ФУНКЦИЙ API*/
	/*РАБОТА С АВТОРИЗАЦИЕЙ*/
	function sms_code($connect, $phone_number, $sms_api_key){

	$result = mysqli_query($connect,'SELECT phone_number FROM user WHERE phone_number = '.$phone_number.'');

	$sms_code = rand(1000, 9999); //Создаём код для отправки по sms
	
		if(mysqli_num_rows($result) > 0) //Если номер телефона уже есть в базе, то записываем код для отправки по sms 
		{

			mysqli_query($connect,'UPDATE user SET sms_code = '.$sms_code.' WHERE phone_number = '.$phone_number.''); //Записываем sms код в базу
			$query = mysqli_query($connect,'SELECT sms_code FROM user WHERE phone_number = '.$phone_number.''); //Вытаскиваем sms код
			
			//$sms_code = mysqli_fetch_array($query);

			//$msg = "Ваш+код+".$sms_code['sms_code'].".";

			//$h = file_get_contents('https://sms.ru/sms/send?api_id='.$sms_api_key.'='.$phone_number.'&msg='.$msg.'&json=0'); //Отправляем sms код на номер телефона
			mysqli_close($connect); //Закрываем подключение к бд

		}else{//Если номера в базе нет

			mysqli_query($connect,'INSERT INTO user SET phone_number = '.$phone_number.''); //Записываем номер в базу с минимальными правами доступа
			mysqli_query($connect,'UPDATE user SET sms_code = '.$sms_code.' WHERE phone_number = '.$phone_number.''); //Записываем sms код в базу
			$query = mysqli_query($connect,'SELECT sms_code FROM user WHERE phone_number = '.$phone_number.''); //Вытаскиваем sms код
			
			//$sms_code = mysqli_fetch_array($query);

			//$msg = "Поздравляем!+Вы+успешно+зарегистрировались+в+системе.+Ваш+код+".$sms_code['sms_code'].".";

			//$h = file_get_contents('https://sms.ru/sms/send?api_id='.$sms_api_key.'='.$phone_number.'&msg='.$msg.'&json=0'); //Отправляем sms код на номер телефона
			mysqli_close($connect); //Закрываем подключение к бд
		}

			$json[] = ['result' => 'ok']; //Говорим о том, что всё успешно
			echo json_encode($json);
	}

	function auth($connect, $sms_code, $phone_number){

		$result = mysqli_fetch_array(mysqli_query($connect,'SELECT sms_code, user_id FROM user WHERE phone_number = '.$phone_number.''));

		if($result['sms_code'] == $sms_code){ //Если sms-код введён верно

			$user_token = hash('ripemd160', $phone_number + rand(1, 1000)); //Создаём токен

			mysqli_query($connect,'INSERT INTO tokens SET token = "'.$user_token.'", user_id = "'.$result['user_id'].'"'); //Записываем токен в базу
			mysqli_query($connect,'UPDATE user SET sms_code = NULL WHERE phone_number = '.$phone_number.''); //Обнуляем sms код в базе

			$user_privileges = mysqli_fetch_array(mysqli_query($connect,'SELECT access_right FROM user WHERE phone_number = '.$phone_number.'')); //Записываем токен в базу

			$json[] = ['token' => $user_token, 'privileges' => $user_privileges['access_right']]; //Отправляем этот код по json
			mysqli_close($connect); //Закрываем подключение к бд
			echo json_encode($json);
			die();

		}else{ //Если нет

			$user_token = NULL; //Кидаем NULL вместо токена
			$json[] = ['token' => $user_token]; //Отправляем этот код по json
			mysqli_close($connect); //Закрываем подключение к бд
			echo json_encode($json);
			die();

		}

	}
	/******************************/


	/*РАБОТА С СЕССИЯМИ*/
	function session_exist($connect, $token){

		$result = mysqli_query($connect,'SELECT token FROM tokens WHERE token = "'.$token.'"');
	
		if(mysqli_num_rows($result) > 0)
		{//Если токен есть
			$json[] = ['session_exist' => 'yes'];
			echo json_encode($json);
			mysqli_close($connect); //Закрываем подключение к бд
			die();
		}else{//Если токена нет
		
			$json[] = ['session_exist' => 'no'];
			echo json_encode($json);
			mysqli_close($connect); //Закрываем подключение к бд
			die();
		}

	}

	function sess_unset($connect, $token){
		mysqli_query($connect,'DELETE FROM tokens WHERE token = "'.$token.'"');
	}
	/*****************/


	/*РАБОТА С ПОЛЬЗОВАТЕЛЬСКИМИ ДАННЫМИ*/
	function get_user_data($connect, $token){
		$user_id = mysqli_fetch_array(mysqli_query($connect,'SELECT user_id FROM tokens WHERE token = "'.$token.'"'));
		$result = mysqli_fetch_array(mysqli_query($connect,'SELECT name, phone_number, organization_name, job, e_mail, sms_mailing FROM user WHERE user_id = "'.$user_id['user_id'].'"'));

		$json[] = ['name' => $result['name'], 'phone_number' => $result['phone_number'], 'organization_name' => $result['organization_name'], 'job' => $result['job'], 'e_mail' => $result['e_mail'], 'sms_mailing' => $result['sms_mailing']];

		echo json_encode($json);

	}

	function user_data_update($connect, $token, $name, $phone_number, $organization_name, $job, $e_mail){ //Обновляет данные пользователя
		$user_id = mysqli_fetch_array(mysqli_query($connect,'SELECT user_id FROM tokens WHERE token = "'.$token.'"'));
		mysqli_query($connect,'UPDATE user SET name = "'.$name.'", phone_number = '.$phone_number.', organization_name = "'.$organization_name.'", job = "'.$job.'", e_mail = "'.$e_mail.'" WHERE user_id = "'.$user_id['user_id'].'"');

		}

	function sms_mailing($connect, $token, $mailing){ //Обновляет данные пользователя
		$user_id = mysqli_fetch_array(mysqli_query($connect,'SELECT user_id FROM tokens WHERE token = "'.$token.'"'));
		mysqli_query($connect,'UPDATE user SET sms_mailing = "'.$mailing.'" WHERE user_id = "'.$user_id['user_id'].'"');
		}
	/************************************/


	/*РАБОТА С ЗАЯВКАМИ*/
	function add_order($connect){ //Добавляет заявку
		


		}

	function delete_order($connect){ //Удаляет заявку
		


		}

	/********************/
	/********************/


?>