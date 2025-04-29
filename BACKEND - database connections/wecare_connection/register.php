<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    $first_name = $_POST['first_name'];
    $middle_name = $_POST['middle_name'];
    $last_name = $_POST['last_name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $phone_number = $_POST['phone_number'];
    $address = $_POST['address'];

    $password_hash = password_hash($password, PASSWORD_BCRYPT);

    require_once 'connection.php';

    // Check for existing email only
    $email_check_query = "SELECT id FROM users WHERE email = '$email' LIMIT 1";
    $email_result = mysqli_query($conn, $email_check_query);
    if (mysqli_num_rows($email_result) > 0) {
        $result["success"] = "0";
        $result["message"] = "Email already exists.";
        echo json_encode($result);
        mysqli_close($conn);
        exit();
    }

    // Insert the new user
    $sql = "INSERT INTO users (first_name, middle_name, last_name, email, password, phone_number, address) 
            VALUES ('$first_name', '$middle_name', '$last_name', '$email', '$password_hash', '$phone_number', '$address')";

    if (mysqli_query($conn, $sql)) {
        $result["success"] = "1";
        $result["message"] = "success";
    } else {
        $result["success"] = "0";
        $result["message"] = "Database insertion error.";
    }

    echo json_encode($result);
    mysqli_close($conn);
}
?>