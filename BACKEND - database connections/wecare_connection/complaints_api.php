<?php
header("Content-Type: application/json");
require 'connection.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!isset($data['resident_id']) || !isset($data['title']) || !isset($data['description'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing required fields']);
        exit;
    }

    $residentId = (int)$data['resident_id'];
    $title = $conn->real_escape_string($data['title']);
    $description = $conn->real_escape_string($data['description']);

    $query = "INSERT INTO complaints (resident_id, title, description, status, created_at) 
              VALUES ($residentId, '$title', '$description', 'pending', NOW())";

    if ($conn->query($query)) {
        echo json_encode(['success' => true, 'message' => 'Complaint submitted successfully']);
    } else {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Database error: ' . $conn->error]);
    }
} elseif ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (!isset($_GET['resident_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'resident_id parameter required']);
        exit;
    }

    $residentId = (int)$_GET['resident_id'];
    $query = "SELECT * FROM complaints WHERE resident_id = $residentId ORDER BY created_at DESC";
    $result = $conn->query($query);

    $complaints = [];
    while ($row = $result->fetch_assoc()) {
        $complaints[] = $row;
    }

    echo json_encode($complaints);
} else {
    http_response_code(405);
    echo json_encode(['success' => false, 'message' => 'Method not allowed']);
}
?>