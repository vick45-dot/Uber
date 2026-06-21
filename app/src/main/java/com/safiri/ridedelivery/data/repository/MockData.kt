package com.safiri.ridedelivery.data.repository

import com.safiri.ridedelivery.data.model.*

/**
 * In-memory seed data so the app runs fully offline (no Firebase billing needed).
 * Content is Nakuru/Kenya-flavoured with emoji visuals for a vibrant offline demo.
 * Flip AppConfig.USE_FIREBASE to true when you move to live cloud data.
 */
object MockData {

    // ---- Restaurants (varied cuisines) ----
    val restaurants = mutableListOf(
        Restaurant(id = "r1", ownerUid = "owner1", name = "Mama Oliech Kitchen",
            description = "Authentic Kenyan home cooking", emoji = "🍲", category = "Swahili",
            priceLevel = 2, rating = 4.7, ratingCount = 320, etaMinutes = 25,
            deliveryFee = 150.0, location = GeoPoint(-0.3031, 36.0800, "Nakuru CBD")),
        Restaurant(id = "r2", ownerUid = "owner2", name = "Nyama Choma Palace",
            description = "Grilled meat specialists", emoji = "🍖", category = "Barbecue",
            priceLevel = 3, rating = 4.5, ratingCount = 210, etaMinutes = 35,
            deliveryFee = 200.0, location = GeoPoint(-0.2900, 36.0700, "Section 58")),
        Restaurant(id = "r3", ownerUid = "owner3", name = "Java House Nakuru",
            description = "Coffee, burgers and breakfast", emoji = "☕", category = "Cafe",
            priceLevel = 3, rating = 4.3, ratingCount = 540, etaMinutes = 20,
            deliveryFee = 180.0, location = GeoPoint(-0.2950, 36.0660, "Westside Mall")),
        Restaurant(id = "r4", ownerUid = "owner4", name = "Pizza Inn",
            description = "Pizza and fast bites", emoji = "🍕", category = "Pizza",
            priceLevel = 2, rating = 4.1, ratingCount = 180, etaMinutes = 30,
            deliveryFee = 160.0, location = GeoPoint(-0.3010, 36.0720, "Kenyatta Avenue")),
        Restaurant(id = "r5", ownerUid = "owner5", name = "Kilimanjaro Bites",
            description = "Fried chicken and chips", emoji = "🍗", category = "Fast Food",
            priceLevel = 2, rating = 4.4, ratingCount = 410, etaMinutes = 22,
            deliveryFee = 140.0, location = GeoPoint(-0.2970, 36.0680, "Mburma Road")),
        Restaurant(id = "r6", ownerUid = "owner6", name = "Spice Route Indian",
            description = "Curries, biryani and naan", emoji = "🍛", category = "Indian",
            priceLevel = 3, rating = 4.6, ratingCount = 150, etaMinutes = 40,
            deliveryFee = 220.0, location = GeoPoint(-0.3050, 36.0640, "Milimani")),
        Restaurant(id = "r7", ownerUid = "owner7", name = "Sweet Tooth Bakery",
            description = "Cakes, pastries and desserts", emoji = "🍰", category = "Dessert",
            priceLevel = 2, rating = 4.8, ratingCount = 290, etaMinutes = 18,
            deliveryFee = 120.0, location = GeoPoint(-0.2990, 36.0710, "Nakuru CBD")),
        Restaurant(id = "r8", ownerUid = "owner8", name = "Green Bowl Healthy",
            description = "Salads, smoothies and wraps", emoji = "🥗", category = "Healthy",
            priceLevel = 3, rating = 4.2, ratingCount = 95, etaMinutes = 25,
            deliveryFee = 170.0, location = GeoPoint(-0.2930, 36.0670, "Westside"))
    )

    // ---- Menu items with categories, emojis, popularity ----
    val menuItems = mutableListOf(
        // Mama Oliech (Swahili)
        MenuItem("m1","r1","Ugali & Beef Stew","Served with sukuma wiki","","🍛","Mains",350.0,popular=true),
        MenuItem("m2","r1","Pilau & Kachumbari","Spiced rice with fresh salad","","🍚","Mains",400.0,popular=true),
        MenuItem("m3","r1","Whole Fried Tilapia","Fresh fish with ugali","","🐟","Mains",550.0),
        MenuItem("m4","r1","Chapati (2 pcs)","Soft layered flatbread","","🫓","Sides",80.0),
        MenuItem("m5","r1","Sukuma Wiki","Braised collard greens","","🥬","Sides",100.0),
        MenuItem("m6","r1","Fresh Mango Juice","Seasonal mango blend","","🥭","Drinks",150.0),
        // Nyama Choma Palace
        MenuItem("m7","r2","Goat Nyama Choma 1kg","Charcoal-grilled goat","","🍖","Mains",1200.0,popular=true),
        MenuItem("m8","r2","Beef Nyama Choma 1kg","Grilled beef cuts","","🥩","Mains",1000.0),
        MenuItem("m9","r2","Mukimo & Roast Chicken","Mashed greens classic","","🍗","Mains",650.0,popular=true),
        MenuItem("m10","r2","Mutura","Traditional sausage","","🌭","Sides",200.0),
        MenuItem("m11","r2","Tusker Soda","Cold soft drink","","🥤","Drinks",120.0),
        // Java House
        MenuItem("m12","r3","Cheeseburger Combo","Burger, fries and soda","","🍔","Mains",750.0,popular=true),
        MenuItem("m13","r3","Chicken & Chips","Quarter chicken meal","","🍟","Mains",600.0),
        MenuItem("m14","r3","Big Breakfast","Eggs, sausage, toast, beans","","🍳","Mains",680.0,popular=true),
        MenuItem("m15","r3","Cappuccino","Freshly brewed coffee","","☕","Drinks",280.0),
        MenuItem("m16","r3","Chocolate Milkshake","Thick and creamy","","🥤","Drinks",350.0),
        // Pizza Inn
        MenuItem("m17","r4","Chicken Tikka Pizza L","Spicy chicken, large","","🍕","Mains",1100.0,popular=true),
        MenuItem("m18","r4","Margherita Pizza M","Classic cheese, medium","","🍕","Mains",850.0),
        MenuItem("m19","r4","BBQ Beef Pizza L","Smoky beef topping","","🍕","Mains",1200.0),
        MenuItem("m20","r4","Garlic Bread","Toasted with herbs","","🥖","Sides",250.0),
        MenuItem("m21","r4","Fanta Orange","Chilled 500ml","","🥤","Drinks",100.0),
        // Kilimanjaro Bites
        MenuItem("m22","r5","Crispy Chicken Bucket","8 pieces to share","","🍗","Mains",950.0,popular=true),
        MenuItem("m23","r5","Chicken Wrap","Grilled wrap with fries","","🌯","Mains",450.0),
        MenuItem("m24","r5","Loaded Fries","Cheese and beef topping","","🍟","Sides",350.0,popular=true),
        MenuItem("m25","r5","Soda 300ml","Assorted flavours","","🥤","Drinks",80.0),
        // Spice Route Indian
        MenuItem("m26","r6","Chicken Biryani","Fragrant basmati rice","","🍛","Mains",750.0,popular=true),
        MenuItem("m27","r6","Butter Chicken","Creamy tomato curry","","🍲","Mains",800.0,popular=true),
        MenuItem("m28","r6","Vegetable Samosa (3)","Crispy fried pastry","","🥟","Sides",200.0),
        MenuItem("m29","r6","Garlic Naan","Tandoor flatbread","","🫓","Sides",150.0),
        MenuItem("m30","r6","Mango Lassi","Yoghurt mango drink","","🥭","Drinks",250.0),
        // Sweet Tooth Bakery
        MenuItem("m31","r7","Black Forest Slice","Cherry chocolate cake","","🍰","Dessert",300.0,popular=true),
        MenuItem("m32","r7","Glazed Doughnut","Classic ring doughnut","","🍩","Dessert",120.0),
        MenuItem("m33","r7","Chocolate Croissant","Buttery and flaky","","🥐","Dessert",180.0),
        MenuItem("m34","r7","Cupcake (Vanilla)","Topped with frosting","","🧁","Dessert",150.0,popular=true),
        // Green Bowl Healthy
        MenuItem("m35","r8","Chicken Caesar Salad","Grilled chicken, crisp greens","","🥗","Mains",550.0,popular=true),
        MenuItem("m36","r8","Avocado Wrap","Veggie wrap with hummus","","🌯","Mains",450.0),
        MenuItem("m37","r8","Berry Smoothie","Mixed berry blend","","🥤","Drinks",350.0,popular=true),
        MenuItem("m38","r8","Fruit Bowl","Seasonal fresh fruit","","🍓","Sides",300.0)
    )

    // ---- Shops ----
    val shops = mutableListOf(
        Shop("s1","owner9","Naivas Supermarket","Groceries","🛒","",4.4,45,GeoPoint(-0.2980,36.0690,"Nakuru CBD")),
        Shop("s2","owner10","Goodlife Pharmacy","Pharmacy","💊","",4.6,30,GeoPoint(-0.2940,36.0710,"Westside Mall")),
        Shop("s3","owner11","Quickmart","Groceries","🛒","",4.2,40,GeoPoint(-0.3020,36.0670,"Kenyatta Avenue")),
        Shop("s4","owner12","Flowers & Gifts","Florist","💐","",4.7,35,GeoPoint(-0.2960,36.0700,"Milimani")),
        Shop("s5","owner13","TechHub Electronics","Electronics","📱","",4.3,50,GeoPoint(-0.3000,36.0650,"CBD"))
    )

    // ---- Drivers (varied vehicles) ----
    val drivers = mutableListOf(
        Driver(uid="d1",name="James Kamau",phone="0712345678",vehicleType=VehicleType.CAR,
            vehicleModel="Toyota Vitz",plateNumber="KDA 123A",rating=4.8,ratingCount=95,
            approval=ApprovalStatus.APPROVED,isOnline=true,
            currentLocation=GeoPoint(-0.3000,36.0750,"Nakuru CBD"),totalEarnings=45200.0),
        Driver(uid="d2",name="Peter Otieno",phone="0723456789",vehicleType=VehicleType.BODA_BODA,
            vehicleModel="Boxer 150",plateNumber="KMEA 456B",rating=4.6,ratingCount=140,
            approval=ApprovalStatus.APPROVED,isOnline=true,
            currentLocation=GeoPoint(-0.2960,36.0720,"Section 58"),totalEarnings=28900.0),
        Driver(uid="d3",name="Grace Wanjiku",phone="0734567890",vehicleType=VehicleType.TAXI,
            vehicleModel="Toyota Fielder",plateNumber="KDB 789C",rating=4.9,ratingCount=67,
            approval=ApprovalStatus.APPROVED,isOnline=true,
            currentLocation=GeoPoint(-0.3040,36.0680,"Westside"),totalEarnings=61000.0),
        Driver(uid="d5",name="Mary Njeri",phone="0756789012",vehicleType=VehicleType.MOTORCYCLE,
            vehicleModel="TVS Apache",plateNumber="KMGA 345E",rating=4.7,ratingCount=88,
            approval=ApprovalStatus.APPROVED,isOnline=true,
            currentLocation=GeoPoint(-0.2980,36.0730,"CBD"),totalEarnings=33400.0),
        // Pending driver for the Admin dashboard demo
        Driver(uid="d4",name="Samuel Kiprotich",phone="0745678901",vehicleType=VehicleType.MOTORCYCLE,
            vehicleModel="TVS Star",plateNumber="KMFA 012D",rating=5.0,ratingCount=0,
            approval=ApprovalStatus.PENDING,isOnline=false,
            currentLocation=GeoPoint(-0.2990,36.0700,"Nakuru"),totalEarnings=0.0)
    )

    val orders = mutableListOf<Order>()
    val rides = mutableListOf<Ride>()
    var currentUser: AppUser? = null
    val users = mutableMapOf<String, AppUser>()

    private var idCounter = 1000
    fun nextId(prefix: String): String = "$prefix${idCounter++}"
}
