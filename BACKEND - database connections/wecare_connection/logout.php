<?php
header('Content-Type: application/json');
require_once 'connection.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = $_POST['user_id'];
    
    // Simple logout - just return success
    echo json_encode(['success' => '1', 'message' => 'Logged out']);
    
    $conn->close();
}
?>