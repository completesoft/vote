<?php


//$fp = fopen("last.txt", "w+");
$filename = "last.txt";
$file_votes = "votes.txt";
$file_messages = "messages.txt";

$datetime = date("Y-m-d H:i:s");

if (isset($_POST['Mark'])){
	$mark = $_POST['Mark'];
	$id = 1; //$_POST['id'];

	file_put_contents($file_votes, $datetime.",".$id.",".$mark."\n", FILE_APPEND | LOCK_EX);

}  

if (isset($_POST['Message'])){
	$message = $_POST['Message'];
	$name = $_POST["Name"];
	$phone = $_POST["Phone"];

	file_put_contents($file_messages, $datetime.",".$name.",".$phone.",".$message."\n", FILE_APPEND | LOCK_EX);


$html = <<< EOF

<!DOCTYPE html>
<html>
 <head>
  <meta charset="utf-8">
  <title>Тег META, атрибут charset</title>
 </head>
 <body> 
  <p>Дата: $datetime</p>
  <p>Имя: $name</p>
  <p>Телефон: $phone</p>
  <p>Сообщение: $message</p>
  
 </body>
</html>

EOF;




	$to      = 'Vote <a.zaharov@product.in.ua>';
	$subject = 'Complaint';
	$message = $html;
	$headers = 'From: Vote <a.zaharov@product.in.ua>' . "\r\n" .
				'MIME-Version: 1.0' . "\r\n".
				'Content-type: text/html; charset=utf-8' . "\r\n".
	    'X-Mailer: PHP/' . phpversion();




	$m = mail($to, $subject, $message, $headers);
	 


}  



  
  
  file_put_contents($filename, "--------------------\n", FILE_APPEND | LOCK_EX);
  file_put_contents($filename, $datetime, FILE_APPEND | LOCK_EX);
  
  file_put_contents($filename, "\n\nGET\n", FILE_APPEND | LOCK_EX);

  foreach($_GET as $key => $value) 
  { 
     
     
     file_put_contents($filename, $key." = ".$value."\n", FILE_APPEND | LOCK_EX);
  } 
  
  
  file_put_contents($filename, "\nPOST\n", FILE_APPEND | LOCK_EX);
  foreach($_POST as $key => $value) 
  { 
    
     
     file_put_contents($filename, $key." = ".$value."\n", FILE_APPEND | LOCK_EX);

  } 



  file_put_contents($filename, "\n\n\n", FILE_APPEND | LOCK_EX);


	echo("ok");


?>