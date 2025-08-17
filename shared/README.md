Kotlin multiplatform module
==========================


Things to note
When migrating from framework to cocoapods, edit your build configurations to iosApp.xcworkspace instead of xcodeproj
https://stackoverflow.com/a/71581472/3884184

Sqlite

#### Migration

Generate schema using `./gradlew :shared:generateDatabaseSchema`

Change the database, update the version and create migration

Generate new schema
And verify migration using `./gradlew :shared:verifyDatabaseMigration`
