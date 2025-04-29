<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = $_POST['email'];
    $password = $_POST['password'];

    require_once 'connection.php';

    // Use Prepared Statement to avoid SQL Injection
    $stmt = $conn->prepare("SELECT id, email, password FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $response = $stmt->get_result();

    $result = array();
    $result['login'] = array();

    if ($response->num_rows === 1) {
        $row = $response->fetch_assoc();

        // Verify hashed password
        if (password_verify($password, $row['password'])) {
            $index['id'] = $row['id'];
            $index['email'] = $row['email'];

            array_push($result['login'], $index);

            $result["success"] = "1";
            $result["message"] = "success";
        } else {
            $result["success"] = "0";
            $result["message"] = "Invalid password";
        }
    } else {
        $result["success"] = "0";
        $result["message"] = "User not found";
    }

    echo json_encode($result);
    $stmt->close();
    $conn->close();
}
?>