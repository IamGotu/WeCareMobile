<?php
header("Content-Type: application/json");
require_once 'connection.php';

$userId = $_GET['user_id'] ?? 0;

$query = "SELECT 
            hc.id, 
            hc.title, 
            hc.description, 
            hc.status, 
            hc.created_at, 
            hc.priority, 
            hc.assigned_personnel, 
            hc.resolution_notes, 
            hc.resolved_at
          FROM history_complaints hc
          WHERE hc.resident_id = ?
          ORDER BY hc.created_at DESC";

$stmt = $conn->prepare($query);
$stmt->bind_param("i", $userId);
$stmt->execute();
$result = $stmt->get_result();

$complaints = [];
while ($row = $result->fetch_assoc()) {
    $complaints[] = $row;
}

echo json_encode([
    'success' => true,
    'complaints' => $complaints
]);

$stmt->close();
$conn->close();
?>