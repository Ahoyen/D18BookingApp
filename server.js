const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config();

const authRoutes = require('./routes/auth');
const categoryRoutes = require('./routes/category')
const roomRoutes = require('./routes/room')

const app = express();
const PORT = process.env.PORT || 5000;
const cookieParser = require('cookie-parser');

// Middleware
app.use(cors());
app.use(cookieParser());
app.use(express.json());

// Kết nối MongoDB
mongoose.connect(process.env.MONGO_URI)
  .then(() => console.log("✅ MongoDB connected"))
  .catch(err => console.error("❌ MongoDB error:", err));

// Route test
app.get('/', (req, res) => {
  res.send('Hello from Node.js API!');
});

// Route auth
app.use(authRoutes);
// Route category
app.use(categoryRoutes)
// Route room
app.use(roomRoutes)

app.use(express.static('public'));

// Start server
app.listen(PORT, () => {
  console.log(`🚀 Server running on port ${PORT}`);
});
