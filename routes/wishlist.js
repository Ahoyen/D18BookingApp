const express = require('express');
const router = express.Router();
const Wishlist = require('../models/Wishlist');
const Room = require('../models/Room');
const User = require('../models/User');

// GET /api/user/wishlists - trả về danh sách chi tiết wishlist (room info)
router.get('/api/user/wishlists', async (req, res) => {
  const sessionId = req.cookies.sessionId;
  const userId = parseInt(sessionId, 10);

  if (!userId) {
    return res.status(401).json({ success: false, error: 'Unauthorized - missing session' });
  }

  try {
    const wishlist = await Wishlist.findOne({ userId });
    if (!wishlist) {
      return res.json({ success: true, data: [], error: null });
    }

    const rooms = await Room.find({
        id: { $in: wishlist.roomIds.map(id => id.toString()) }
      });
    const data = rooms.map(room => ({
      id: room.id,
      images: room.thumbnail || '',
    }));

    res.json({ success: true, data, error: null });
  } catch (error) {
    res.status(500).json({ success: false, data: null, error: error.message });
  }
});

// GET /api/user/wishlists/ids - chỉ trả về id của phòng đã thích
router.get('/api/user/wishlists/ids', async (req, res) => {
  const sessionId = req.cookies.sessionId;
  const userId = parseInt(sessionId, 10);

  if (!userId) {
    return res.status(401).json({ success: false, error: 'Unauthorized - missing session' });
  }

  try {
    const wishlist = await Wishlist.findOne({ userId });
    const data = wishlist ? wishlist.roomIds.map(id => id.toString()) : [];
    res.json({ success: true, data, error: null });
  } catch (error) {
    res.status(500).json({ success: false, data: null, error: error.message });
  }
});

// PUT /api/user/add-to-favoritelists/:id - thêm 1 phòng vào wishlist
router.put('/api/user/add-to-favoritelists/:id', async (req, res) => {
  const sessionId = req.cookies.sessionId;
  const userId = parseInt(sessionId, 10);

  if (!userId) {
    return res.status(401).json({ success: false, error: 'Unauthorized - missing session' });
  }

  try {
    const roomId = parseInt(req.params.id, 10);
    if (isNaN(roomId)) {
      return res.status(400).json({ success: false, error: 'Invalid room ID' });
    }
    console.log(`[DEBUG] User ${userId} adding room ${roomId} to wishlist`);
    let wishlist = await Wishlist.findOne({ userId });
    if (!wishlist) {
      console.log('[DEBUG] Wishlist not found, creating new');
      wishlist = new Wishlist({ userId, roomIds: [] });
    }

    if (!wishlist.roomIds.includes(roomId)) {
      wishlist.roomIds.push(roomId);
      await wishlist.save();
      console.log('[DEBUG] Room added successfully');
    }else {
      console.log('[DEBUG] Room already in wishlist');
    }

    res.json({ success: true, data: "Added successfully", error: null });
  } catch (error) {
    console.error('[ERROR]', error);
    res.status(500).json({ success: false, data: null, error: error.message });
  }
});

// PUT /api/user/remove-from-favoritelists/:id - xóa 1 phòng khỏi wishlist
router.put('/api/user/remove-from-favoritelists/:id', async (req, res) => {
  const sessionId = req.cookies.sessionId;
  const userId = parseInt(sessionId, 10);

  if (!userId) {
    return res.status(401).json({ success: false, error: 'Unauthorized - missing session' });
  }

  try {
    const roomId = parseInt(req.params.id, 10);
    if (isNaN(roomId)) {
      return res.status(400).json({ success: false, error: 'Invalid room ID' });
    }

    const wishlist = await Wishlist.findOne({ userId });
    if (!wishlist) {
      return res.status(404).json({ success: false, data: null, error: "Wishlist not found" });
    }

    wishlist.roomIds = wishlist.roomIds.filter(id => id !== roomId);
    await wishlist.save();

    res.json({ success: true, data: "Removed successfully", error: null });
  } catch (error) {
    res.status(500).json({ success: false, data: null, error: error.message });
  }
});

module.exports = router;
