<?php
$conn = mysqli_connect("localhost", "root", "", "wecare");

if (!$conn) {
    die("Database connection failed: " . mysqli_connect_error());
}
?>