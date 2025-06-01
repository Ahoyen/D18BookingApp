module.exports = function(req, res, next) {
    const sessionId = req.cookies.sessionId;
    const userId = parseInt(sessionId, 10);
  
    if (!userId) {
      return res.status(401).json({ success: false, error: 'Unauthorized - missing session' });
    }
  
    // Giả sử bạn lưu userId trong sessionId, đặt userId vào req.user để dùng tiếp
    req.user = { _id: userId };
  
    next();
  };