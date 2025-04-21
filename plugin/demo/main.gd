extends Node2D

# TODO: Update to match your plugin's name
var _plugin_name = "GodotAndroidPluginFitSteps"
var _android_plugin

func _ready():
	if Engine.has_singleton(_plugin_name):
		_android_plugin = Engine.get_singleton(_plugin_name)
		OS.request_permissions()
		
	else:
		printerr("Couldn't find plugin " + _plugin_name)

func _on_Button_pressed():
	if _android_plugin:
		_android_plugin.connectToRecordingAPI()
		
	else:
		printerr("Couldn't find plugin " + _plugin_name)


func _on_button_2_pressed():
	if _android_plugin:
		_android_plugin.readData()
		
	else:
		printerr("Couldn't find plugin " + _plugin_name)


func _on_button_3_pressed():
	if _android_plugin:
		var dataset = _android_plugin.dumpDataSet()
		print(dataset)
		
	else:
		printerr("Couldn't find plugin " + _plugin_name)
