<?php
header('Content-Type: application/json');
require_once 'connection.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = $_POST['user_id'];
    $role = $_POST['role'];
    $limit = isset($_POST['limit']) ? (int)$_POST['limit'] : 10;

    $response = ['success' => '0', 'message' => 'Invalid request'];

    // Validate user exists
    $stmt = $conn->prepare("SELECT id FROM users WHERE id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        // Get complaints based on user role
        if ($role == 'admin') {
            $result = $conn->query("
                SELECT c.*, 
                       CONCAT(u.first_name, ' ', u.last_name) as resident_name,
                       CONCAT(o.first_name, ' ', o.last_name) as officer_name
                FROM complaints c
                LEFT JOIN users u ON c.resident_id = u.id
                LEFT JOIN users o ON c.assigned_officer_id = o.id
                ORDER BY c.created_at DESC
                LIMIT $limit
            ");
        } else if ($role == 'officer') {
            $result = $conn->query("
                SELECT c.*, 
                       CONCAT(u.first_name, ' ', u.last_name) as resident_name
                FROM complaints c
                LEFT JOIN users u ON c.resident_id = u.id
                WHERE c.assigned_officer_id = $user_id
                ORDER BY c.created_at DESC
                LIMIT $limit
            ");
        } else { // resident
            $result = $conn->query("
                SELECT c.*, 
                       CONCAT(o.first_name, ' ', o.last_name) as officer_name
                FROM complaints c
                LEFT JOIN users o ON c.assigned_officer_id = o.id
                WHERE c.resident_id = $user_id
                ORDER BY c.created_at DESC
                LIMIT $limit
            ");
        }

        $complaints = [];
        while ($row = $result->fetch_assoc()) {
            $complaints[] = [
                'id' => $row['id'],
                'title' => $row['title'],
                'status' => $row['status'],
                'priority' => $row['priority'],
                'created_at' => $row['created_at'],
                'assigned_personnel' => $row['officer_name'] ?? $row['assigned_personnel'] ?? 'Unassigned',
                'resident_name' => $row['resident_name'] ?? ''
            ];
        }

        $response = [
            'success' => '1',
            'complaints' => $complaints
        ];
    }

    echo json_encode($response);
    $conn->close();
}
?>