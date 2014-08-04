<?php
  function interpret_file() {
    $lines = file("config", FILE_IGNORE_NEW_LINES);
    $config = array();

    foreach ($lines as $line) {
      $command = strtok($line, " ");
      $value = strtok(" ");
      $config[$command] = $value;
    }

    return $config;
  }

  function make_default() {
    return array(
      "extendstart" => "true",
      "starttime" => "15",
      "extendend" => "true",
      "endtime" => "15",
      "candelete" => "true",
      "canend" => "true",
      "enddelete" => "true",
      "windowsize" => "60",
      "delaydelete" => "false"
    );
  }

  if file_exists("config") {
    $settings = interpret_file();
  } else {
    $settings = make_default();
  }
?>
<html>
  <head>
    <title>Global configuration management</title>
  </head>
  <body>
    <h1>Global configuration management</h1>
  </body>
  <form name="config" action="save_settings.php" method="post">
    <table>
      <tr>
        <td>
          Extend start time
        </td>
        <td>
<?php
  if ($settings["extendstart"] == "true") {
    echo
      '          : <input type="checkbox" name="extendstart" checked>';
  } else {
    echo
      '          : <input type="checkbox" name="extendstart">';
?>
        </td>
      </tr>
      <tr>
        <td>
          Extend start by
        </td>
        <td>
          :
          <select name="starttime">
<?php
  if ($settings["endtime"] == "15") {
    echo
      '            <option value="15" selected>15</option>';
  } else {
    echo
      '            <option value="15">15</option>';
  }
  if ($settings["endtime"] == "30") {
    echo
      '            <option value="30" selected>30</option>';
  } else {
    echo
      '            <option value="30">30</option>';
  }
  if ($settings["endtime"] == "45") {
    echo
      '            <option value="45" selected>45</option>';
  } else {
    echo
      '            <option value="45">45</option>';
  }
  if ($settings["endtime"] == "60") {
    echo
      '            <option value="60" selected>60</option>';
  } else {
    echo
      '            <option value="60">60</option>';
  }
?>
          </select>
          Minutes
        </td>
      </tr>
      <tr>
        <td>
          Extend end time
        </td>
        <td>
<?php
  if ($settings["extendend"] == "true") {
    echo
      '          : <input type="checkbox" name="extendend" checked>';
  } else {
    echo
      '          : <input type="checkbox" name="extendend">';
?>
        </td>
      </tr>
      <tr>
        <td>
          Extend end by
        </td>
        <td>
          :
          <select name="endtime">
<?php
  if ($settings["endtime"] == "15") {
    echo
      '            <option value="15" selected>15</option>';
  } else {
    echo
      '            <option value="15">15</option>';
  }
  if ($settings["endtime"] == "30") {
    echo
      '            <option value="30" selected>30</option>';
  } else {
    echo
      '            <option value="30">30</option>';
  }
  if ($settings["endtime"] == "45") {
    echo
      '            <option value="45" selected>45</option>';
  } else {
    echo
      '            <option value="45">45</option>';
  }
  if ($settings["endtime"] == "60") {
    echo
      '            <option value="60" selected>60</option>';
  } else {
    echo
      '            <option value="60">60</option>';
  }
?>
          </select>
          Minutes
        </td>
      </tr>
      <tr>
        <td>
          Can delete
        </td>
        <td>
<?php
  if ($settings["candelete"] == "true") {
    echo
      '          : <input type="checkbox" name="candelete" checked>';
  } else {
    echo
      '          : <input type="checkbox" name="candelete">';
?>
        </td>
      </tr>
      <tr>
        <td>
          Can end
        </td>
        <td>
<?php
  if ($settings["canend"] == "true") {
    echo
      '          : <input type="checkbox" name="canend" checked>';
  } else {
    echo
      '          : <input type="checkbox" name="canend">';
?>
        </td>
      </tr>
      <tr>
        <td>
          End delete
        </td>
        <td>
<?php
  if ($settings["enddelete"] == "true") {
    echo
      '          : <input type="checkbox" name="enddelete" checked>';
  } else {
    echo
      '          : <input type="checkbox" name="enddelete">';
?>
        </td>
      </tr>
      <tr>
        <td>
          Size of TimeWindows
        </td>
        <td>
          :
          <select name="endtime">
<?php
  if ($settings["endtime"] == "15") {
    echo
      '            <option value="15" selected>15</option>';
  } else {
    echo
      '            <option value="15">15</option>';
  }
  if ($settings["endtime"] == "30") {
    echo
      '            <option value="30" selected>30</option>';
  } else {
    echo
      '            <option value="30">30</option>';
  }
  if ($settings["endtime"] == "45") {
    echo
      '            <option value="45" selected>45</option>';
  } else {
    echo
      '            <option value="45">45</option>';
  }
  if ($settings["endtime"] == "60") {
    echo
      '            <option value="60" selected>60</option>';
  } else {
    echo
      '            <option value="60">60</option>';
  }
?>
          </select>
          Minutes
        </td>
      </tr>
      <tr>
        <td>
          Delay delete
        </td>
        <td>
<?php
  if ($settings["delaydelete"] == "true") {
    echo
      '          : <input type="checkbox" name="delaydelete" checked>';
  } else {
    echo
      '          : <input type="checkbox" name="delaydelete">';
?>
        </td>
      </tr>
      <tr>
        <td>
          <input type="submit" value="Save configuration">
        </td>
      </tr>
    </table>
  </form>
</html>
