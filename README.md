# tRip - Low coupling module development
tRip intent to be a practical and lightweight tool to provide modularity and low coupling on your source code.

### Main Features
tRip was designed to:
- Take care of Singleton's and Stateless' services for your
- Allow you to create modules and extensions to your software without change one LOC in the core implementation
- Fast warm-up: tRip already knows what to provide in compilation phase, there's no need to look into the entire class-path for provided classes
- Zero configuration: just let the [ServiceProvider](https://github.com/Skullabs/tRip/wiki/Intro:-My-First-Modular-App) run the software for you
- Factory-based creation of services: you can take control of how a service is provided creating your own factory
- Manually provided data: you still can provide data manually to your software context.

### Low footprint
tRip is basically two jars:
- trip-core ( 30kb ): which is needed to run your application
- trip-processor ( 28kb ): which is responsible by the auto discovery of Singleton's and Stateless' services on your modules. This dependency is needed only during compilation phase.

### Lets get started?
Here is a little tour of tRip main features.
- [Configuring a project with tRip](https://github.com/Skullabs/tRip/wiki/Configuring-a-project-with-tRip)
- [My First Modular App](https://github.com/Skullabs/tRip/wiki/Getting-Started:-My-First-Modular-App)
- [Understanding ServiceProvider](https://github.com/Skullabs/tRip/wiki/Undertanding-ServiceProvider)
- [The Injection Process](https://github.com/Skullabs/tRip/wiki/The-Injection-Process)
- [Provinding Services](https://github.com/Skullabs/tRip/wiki/Providing-Services)
  - [Automatically](https://github.com/Skullabs/tRip/wiki/Providing-Services:-Automatically-Way)
  - [Manually](https://github.com/Skullabs/tRip/wiki/Providing-Services:-Manually-Way)

### License
tRip is Apache 2.0 licensed.
