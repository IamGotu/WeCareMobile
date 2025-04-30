<?php
header("Content-Type: application/json");
require 'connection.php';

$userId = $_GET['user_id'] ?? 0;

// Validate user ID
if (!is_numeric($userId) || $userId <= 0) {
    http_response_code(400);
    echo json_encode(['error' => 'Invalid user ID']);
    exit;
}

// Prepare response
$response = [
    'statistics' => [
        'total_complaints' => 0,
        'pending_complaints' => 0,
        'resolved_this_month' => 0,
        'in_progress_complaints' => 0
    ],
    'active_complaints' => [],
    'resolved_complaints' => []
];

try {
    // Get statistics
    $statsQuery = "SELECT 
        COUNT(*) as total_complaints,
        SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as pending_complaints,
        SUM(CASE WHEN status = 'resolved' AND MONTH(resolved_at) = MONTH(CURRENT_DATE()) THEN 1 ELSE 0 END) as resolved_this_month,
        SUM(CASE WHEN status = 'in_progress' THEN 1 ELSE 0 END) as in_progress_complaints
        FROM complaints 
        WHERE resident_id = ?";
    
    $stmt = $conn->prepare($statsQuery);
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $statsResult = $stmt->get_result()->fetch_assoc();
    
    $response['statistics'] = [
        'total_complaints' => $statsResult['total_complaints'] ?? 0,
        'pending_complaints' => $statsResult['pending_complaints'] ?? 0,
        'resolved_this_month' => $statsResult['resolved_this_month'] ?? 0,
        'in_progress_complaints' => $statsResult['in_progress_complaints'] ?? 0
    ];

    // Get active complaints (pending + in_progress)
    $activeQuery = "SELECT id, title, description, status, created_at 
                   FROM complaints 
                   WHERE resident_id = ? AND status IN ('pending', 'in_progress')
                   ORDER BY created_at DESC 
                   LIMIT 5";
    
    $stmt = $conn->prepare($activeQuery);
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $activeResult = $stmt->get_result();
    
    while ($row = $activeResult->fetch_assoc()) {
        $response['active_complaints'][] = $row;
    }

    // Get resolved complaints
    $resolvedQuery = "SELECT id, title, description, status, created_at, resolved_at 
                     FROM complaints 
                     WHERE resident_id = ? AND status = 'resolved'
                     ORDER BY resolved_at DESC 
                     LIMIT 5";
    
    $stmt = $conn->prepare($resolvedQuery);
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $resolvedResult = $stmt->get_result();
    
    while ($row = $resolvedResult->fetch_assoc()) {
        $response['resolved_complaints'][] = $row;
    }

    echo json_encode($response);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}