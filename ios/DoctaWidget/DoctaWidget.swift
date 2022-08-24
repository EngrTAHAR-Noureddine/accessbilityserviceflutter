import WidgetKit
import SwiftUI

struct Counter :Codable{
    
    let count: Int
    
    init( a: Int){
    
        count = a
    }
}


struct Provider: TimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(date: Date(),count: 0)
    }

    func getSnapshot(in context: Context, completion: @escaping (SimpleEntry) -> ()) {
        let entry = SimpleEntry(date: Date(),count: 0)
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        var entries: [SimpleEntry] = []

        let encodedData  = UserDefaults(suiteName: "group.com.example.accessbilityserviceflutter")!.object(forKey: "counter") as? Data
         
         var count = Counter(a: 0)
         
         /* Decoding it using JSONDecoder*/
        if let counterEncoded = encodedData {
             
          
         let counterDecoded = try? JSONDecoder().decode(Counter.self, from: counterEncoded)
             
         if let counter = counterDecoded{
                 // You successfully retrieved your car object!
             
                 count = counter
             
             }
         }
        
        
        
        
        let currentDate = Date()
        for hourOffset in 0 ..< 5 {
            
            let entryDate = Calendar.current.date(byAdding: .hour, value: hourOffset, to: currentDate)!
            
            let entry = SimpleEntry(date: entryDate,count: count.count)
            
            entries.append(entry)
        }

        let timeline = Timeline(entries: entries, policy: .atEnd)
        completion(timeline)
    }
}

struct SimpleEntry: TimelineEntry {
    var date: Date
    var count : Int
}

struct DoctaWidgetEntryView : View {
    var entry: Provider.Entry

    var body: some View {
        ViewForSystemMedium(entry: entry)
    }
    
    
    
    struct ViewForSystemMedium: View {
        var entry: Provider.Entry

        var body: some View {
            VStack {
                Text("Counter").foregroundColor(.white)
                Text("\(entry.count)").foregroundColor(.white)
            }.padding()
        }
    }
}




@main
struct DoctaWidget: Widget {
    let kind: String = "DoctaWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            DoctaWidgetEntryView(entry: entry)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color.black)
        }
        .configurationDisplayName("My Widget")
        .description("This is an example widget.")
        .supportedFamilies([.systemMedium])
    }
}

struct DoctaWidget_Previews: PreviewProvider {
    static var previews: some View {
        DoctaWidgetEntryView(entry: SimpleEntry(date: Date(),count: 0))
            .previewContext(WidgetPreviewContext(family: .systemMedium))
    }
}
