const express = require('express');
const router = express.Router();
const Booking = require('../models/Booking');
const Room = require('../models/Room');
const Review = require('../models/Review');

router.get('/api/booking/:roomid/create', async (req, res) => {
  const roomId = parseInt(req.params.roomid);
  const { checkin, checkout, numberOfDays, clientMessage } = req.query;

  // Lấy userId từ cookie
  const sessionId = req.cookies.sessionId; 
  const userId = parseInt(sessionId, 10);

  if (!userId) {
    return res.status(401).json({ success: false, error: 'Unauthorized - missing session' });
  }

  if (!checkin || !checkout || !numberOfDays) {
    return res.status(400).json({ success: false, error: 'Missing required fields' });
  }

  try {
    const room = await Room.findOne({ id: roomId });
    if (!room) {
      return res.status(404).json({ success: false, error: 'Room not found' });
    }
    const review = await Review.findOne({ roomId, userId }); 
    const pricePerDay = room.price;
    const siteFee = 10; 
    const totalFee = pricePerDay * parseInt(numberOfDays) + siteFee;
    const lastBooking = await Booking.findOne().sort({ id: -1 }).lean();
    const nextId = lastBooking && typeof lastBooking.id === 'number' ? lastBooking.id + 1 : 1;

    const booking = new Booking({
      id: nextId,
      bookingDate: new Date().toISOString().split('T')[0],
      checkinDate: checkin,
      checkoutDate: checkout,
      pricePerDay,
      numberOfDays,
      siteFee,
      roomId,
      userId, 
      totalFee,
      currencySymbol: "₫",
      lastUpdated: Math.floor(Date.now() / 1000),
      bookingReview: review ? review.comment : null,
      reviewRating: review ? review.reviewRating : null,
      refund: false,
      complete: false
    });

    await booking.save();

    res.json({
      success: true,
      data: {
        id: booking.id,
        bookingDate: booking.bookingDate,
        currencySymbol: booking.currencySymbol,
        totalFee: booking.totalFee,
        lastUpdated: booking.lastUpdated
      },
      error: null
    });
  } catch (err) {
    res.status(500).json({ success: false, error: err.message });
  }
});

module.exports = router;
