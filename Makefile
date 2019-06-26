default: clean build

.PHONY: clean
clean:
	./gradlew clean

.PHONY: build
build: clean
	./gradlew build

.PHONY: publish
publish: build
	./gradlew publishPlugins