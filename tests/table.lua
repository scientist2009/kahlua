local t = {}

for i = 1, 100 do
   t[i] = i * 3
end

assert(next(t), "next must not be nil")

do
   local c = 0
   for k in pairs(t) do
      c = c + 1
   end
   assert(c == 100, "wrong number of elements in table")

end

for k, v in pairs(t) do
   assert(t[k] == v)
   assert(t[k] == 3 * k)
end

local a, b = -1, 0
t[a * b] = -1
t[b] = 1

assert(t[a * b] == 1)


rawset(t, "hello", "world")
assert(rawget(t, "hello") == "world")
setmetatable(t, {__index = function() return nil end, __newindex = function() end})
assert(rawget(t, "hello") == "world")
rawset(t, "hello", "WORLD")
assert(rawget(t, "hello") == "WORLD")
