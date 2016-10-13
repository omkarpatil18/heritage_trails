
</script>
<?php
	if(isset($_GET['locname'])) {
		try {
		include 'config.php';
		$locname=$_GET['locname'];
		//echo "TYPE :::::: "+$type+"  :::::   ";
		$conn= new PDO("mysql:host=$servername;dbname=$database;charset:utf8",$username,$password);
		$conn-> setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		$conn-> setAttribute(PDO::ATTR_EMULATE_PREPARES, false);

		$stmt = $conn->prepare("SELECT * FROM instimaps WHERE locname LIKE '%$locname%' OR locdescrip LIKE '%$locname%' ");
		$stmt->execute();

		while($row= $stmt->fetch(PDO::FETCH_ASSOC)) {

			$lat = $row["lat"];
			$long = $row["lng"];
			$location_name = $row["locname"];
			$location_description = $row["locdescrip"];
			$arr[] = array("locname"=>$location_name,"locdesc"=>$location_description,"lat"=>$lat,"long"=>$long);
			//echo $location_name."::::".$location_description."::::".$lat."::::".$long;
			//echo "\n";
		}
		echo json_encode($arr);
		} catch (PDOException $e) {
			echo "No Suggestions";
		}

  }
  else{
  	echo "Please set the location name";
  }
?>