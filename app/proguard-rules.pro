-dontwarn com.badlogic.gdx.graphics.g2d.freetype.FreetypeBuild
-dontwarn com.badlogic.gdx.physics.box2d.utils.Box2DBuild
-dontwarn com.badlogic.gdx.physics.bullet.BulletBuild
-dontwarn com.badlogic.gdx.utils.GdxBuild


-keepnames class com.badlogic.gdx.backends.android.AndroidInput*
-keepclassmembers class com.badlogic.gdx.backends.android.AndroidInput* {<init>(...);}
-keep class com.badlogic.gdx.physics.box2d.B2ContactListener