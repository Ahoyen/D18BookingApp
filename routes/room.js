const express = require('express');
const mongoose = require('mongoose');
const router = express.Router();
const Room = require('../models/Room');
const Amenity = require('../models/Amenity');
const Rule = require('../models/Rule');
const User = require('../models/User');

// router.post('/api/rooms', async (req, res) => {
//     try {
//       const { name, categoryId, thumbnail, images, likedByUsers, price, currencySymbol, stayType } = req.body;
  
//       if (!name || typeof categoryId !== 'number') {
//         return res.status(400).json({ success: false, message: 'Room name and categoryId (number) are required' });
//       }
  
//       // Lấy id tăng dần cho Room
//       const lastRoom = await Room.findOne().sort({ id: -1 }).lean();
//       const nextId = lastRoom && typeof lastRoom.id === 'number' ? lastRoom.id + 1 : 1;
  
//       const newRoom = new Room({
//         id: nextId,
//         name,
//         categoryId, 
//         thumbnail,
//         images,
//         likedByUsers,
//         price,
//         currencySymbol,
//         stayType,
//       });
  
//       await newRoom.save();
//       res.status(201).json({ success: true, data: newRoom });
//     } catch (err) {
//       console.error(err);
//       res.status(500).json({ success: false, message: 'Server error' });
//     }
//   });
router.post('/api/rooms', async (req, res) => {
  try {
    const {
      name, categoryId, thumbnail, images, likedByUsers, price, currencySymbol, currencyUnit,
      stayType, description, latitude, longitude, location, cityName,
      guest, bedroom, bathroom, bed, amenities, rules, host
    } = req.body;

    if (!name || typeof categoryId !== 'number') {
      return res.status(400).json({ success: false, message: 'Room name and categoryId (number) are required' });
    }

    // Tìm id tiếp theo
    const lastRoom = await Room.findOne().sort({ id: -1 }).lean();
    const nextId = lastRoom && typeof lastRoom.id === 'number' ? lastRoom.id + 1 : 1;
    const defaultAmenities = [1, 2, 4, 5]; 
    const defaultRules = [1, 2]; 
    const newRoom = new Room({
      id: nextId,
      name,
      categoryId,
      thumbnail,
      images,
      likedByUsers,
      price,
      currencySymbol,
      currencyUnit,
      stayType,
      description,
      latitude,
      longitude,
      location,
      cityName,
      guest,
      bedroom,
      bathroom,
      bed,  
      amenities: amenities && amenities.length > 0 ? amenities : defaultAmenities,
      rules: rules && rules.length > 0 ? rules : defaultRules,
      host       
    });

    await newRoom.save();
    res.status(201).json({ success: true, data: newRoom });
  } catch (err) {
    console.error(err);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});
  

// GET all rooms
router.get('/api/rooms', async (req, res) => {
    try {
      const { categoryId, query } = req.query;
      const filter = {};
  
      if (categoryId && !isNaN(categoryId)) {
        filter.categoryId = parseInt(categoryId); 
      }
  
      if (query) {
        filter.name = { $regex: query, $options: 'i' };
      }
  
      const rooms = await Room.find(filter);
      res.json({ success: true, data: rooms });
    } catch (err) {
      console.error(err);
      res.status(500).json({ success: false, message: 'Server error' });
    }
  });

// GET rooms detail 
  router.get('/api/rooms/:id', async (req, res) => {
    try {
      const id = parseInt(req.params.id);
      const room = await Room.findOne({ id })
        .populate('amenities')
        .populate('rules')
        .populate('host', 'id name avatar') 
        .lean();
  
      if (!room) {
        return res.status(404).json({ success: false, message: 'Room not found' });
      }
  
      // Lấy reviews theo roomId
      const reviews = await Review.find({ roomId: id }).lean();
  
      // Lấy danh sách ngày đã đặt (bookedDates) theo roomId
      const bookings = await Booking.find({ roomId: id }).lean();
      const bookedDates = bookings.map(booking => ({
        checkIn: booking.checkIn,
        checkOut: booking.checkOut
      }));
  
      const averageRating =
        reviews.length > 0
          ? reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length
          : 0;
  
      const response = {
        ...room,
        reviews,
        bookedDates,
        averageRating
      };
  
      res.json({ success: true, data: response });
    } catch (err) {
      console.error(err);
      res.status(500).json({ success: false, message: 'Server error' });
    }
  });
  
module.exports = router;