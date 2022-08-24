import UIKit
import Flutter
import WidgetKit

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    
    private let CHANNEL = "test"
    
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
      
      
      
      
      let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
      
      let batteryChannel = FlutterMethodChannel(name: CHANNEL,
                                                binaryMessenger: controller.binaryMessenger)
      batteryChannel.setMethodCallHandler({
        (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
       
          print( " HI TAHAR " )
          
          guard call.method == "getResult" else {
              result(FlutterMethodNotImplemented)
              return
            }
          
          let args = call.arguments as? Dictionary<String, Any>
          
          var co :Int = 0
          
          if args != nil {
        
              if let num = Int(args!["count"] as! String) {
                  co = num
              } else {
                  co = 0
              }
              
          }
          
          //print("co is : \(co)")
          
          self.counter(c: co)
          
      })

      
      
    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
      
  }
    
    
    func counter(c: Int) {
        
        /* Since it's Codable, we can convert it to JSON using JSONEncoder */
        let counter = Counter(a: c)
        
        let counterData = try! JSONEncoder().encode(counter)
        
        /* ...and store it in your shared UserDefaults container */
        UserDefaults(suiteName: "group.com.example.accessbilityserviceflutter")!.set(counterData, forKey: "counter")
        let index = Int(c) % 3
        
        let personData = try! JSONEncoder().encode(Person.sampleData[index])
        UserDefaults(suiteName: "group.com.example.accessbilityserviceflutter")!.set(personData, forKey: "person")
        
       
        WidgetCenter.shared.reloadAllTimelines()
       
       
    }
    
    
}

struct Counter :Codable{
    
    let count: Int
    
    init( a: Int){
    
        count = a
    }
}

struct Person :Codable{
    
    let name: String
    let age: Int
    
    init(n : String, a: Int){
        name = n
        age = a
    }
    
}
extension Person {
    
    static let sampleData: [Person] = [
    
        Person(n: "Abdelkader", a: 34),
        Person(n: "Samir", a: 90),
        Person(n: "Rachid", a: 78),
        
    ]
    
}
