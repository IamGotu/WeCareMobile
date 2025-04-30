<?php
header("Content-Type: application/json");
require_once 'connection.php';

// Log received data
file_put_contents('login_log.txt', print_r($_POST, true), FILE_APPEND);

$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

// Verify both fields were received
if (empty($email) || empty($password)) {
    echo json_encode(['success' => '0', 'message' => 'Email and password required']);
    exit;
}

$query = "SELECT id, email, role, password FROM users WHERE email = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    
    // Verify password (assuming passwords are hashed)
    if (password_verify($password, $row['password'])) {
        $response = [
            'success' => '1',
            'login' => [
                [
                    'id' => $row['id'],
                    'email' => $row['email'],
                    'role' => $row['role']
                ]
            ]
        ];
    } else {
        $response = ['success' => '0', 'message' => 'Invalid password'];
    }
} else {
    $response = ['success' => '0', 'message' => 'User not found'];
}

file_put_contents('login_log.txt', "\nResponse: ".print_r($response, true), FILE_APPEND);
echo json_encode($response);
?>