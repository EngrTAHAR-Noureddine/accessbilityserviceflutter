//
//  WidgetDoctaTwo.swift
//  WidgetDoctaTwo
//
//  Created by clever zone on 24/8/2022.
//

import WidgetKit
import SwiftUI


struct Person :Codable{
    
    let name: String
    let age: Int
    
    init(n : String, a: Int){
        name = n
        age = a
    }
    
}

struct Provider: TimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(date: Date(),name: "InitName", age: 0)
    }

    func getSnapshot(in context: Context, completion: @escaping (SimpleEntry) -> ()) {
        let entry = SimpleEntry(date: Date(),name: "InitName", age: 0)
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        var entries: [SimpleEntry] = []

        let encodedData  = UserDefaults(suiteName: "group.com.example.accessbilityserviceflutter")!.object(forKey: "person") as? Data
         
         var p = Person(n: "NAME", a: 0)
         
         /* Decoding it using JSONDecoder*/
        if let personEncoded = encodedData {
             
          
         let personDecoded = try? JSONDecoder().decode(Person.self, from: personEncoded)
             
         if let person = personDecoded{
                 // You successfully retrieved your car object!
             
                 p = person
             
             }
         }
        
        
        
        let currentDate = Date()
        for hourOffset in 0 ..< 5 {
            let entryDate = Calendar.current.date(byAdding: .hour, value: hourOffset, to: currentDate)!
            let entry = SimpleEntry(date: entryDate,name: p.name, age: p.age)
            entries.append(entry)
        }

        let timeline = Timeline(entries: entries, policy: .atEnd)
        completion(timeline)
    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    var name : String
    var age : Int
}

struct WidgetDoctaTwoEntryView : View {
    var entry: Provider.Entry

    var body: some View {
        ViewForSystemLarge(entry: entry)
    }
    
    
    struct ViewForSystemLarge: View {
        var entry: Provider.Entry

        var body: some View {
            VStack {
                Text("Person").foregroundColor(.white)
                Text(entry.name).foregroundColor(.white)
                Text(String(entry.age)).foregroundColor(.white)
            }.padding()
        }
    }
}

@main
struct WidgetDoctaTwo: Widget {
    let kind: String = "WidgetDoctaTwo"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            WidgetDoctaTwoEntryView(entry: entry)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color.blue)
        }
        .configurationDisplayName("My Widget")
        .description("This is an example widget.")
        .supportedFamilies([.systemLarge])
    }
}

struct WidgetDoctaTwo_Previews: PreviewProvider {
    static var previews: some View {
        WidgetDoctaTwoEntryView(entry: SimpleEntry(date: Date(),name: "InitName", age:0))
            .previewContext(WidgetPreviewContext(family: .systemLarge))
    }
}
