// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "SmsDetector",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "SmsDetector",
            targets: ["SmsDetectorPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "SmsDetectorPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/SmsDetectorPlugin"),
        .testTarget(
            name: "SmsDetectorPluginTests",
            dependencies: ["SmsDetectorPlugin"],
            path: "ios/Tests/SmsDetectorPluginTests")
    ]
)