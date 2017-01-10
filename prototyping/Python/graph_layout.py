import sys
import math
import time
import random
import copy
from PIL import Image, ImageDraw
from Segment import Segment
from Circle import Circle

class BackTrackInfo:
		'''
		vertex : Vertex
		index1 : int
		index2 : int
		'''

		def __init__(self, vertex, index1, index2):
			self.vertex = vertex
			self.index1 = index1
			self.index2 = index2

class AnnealingSimulator:
	'''
	current_state : EnhancedTree
	time_limit : float (seconds)
	initial_temperature : float
	temperature_step : int
	decrease_factor : float
	backtrack_info : BackTrackInfo
	'''

	def __init__(self, initial_state, time_limit, temperature=1.0, 
		decrease_factor=0.8, temperature_step=10):
			self.current_state = initial_state
			self.time_limit = time_limit
			self.initial_temperature = temperature
			self.decrease_factor = decrease_factor
			self.temperature_step = temperature_step
			self.backtrack_info = None

# 	def currentCost(self):
# 		'''
# 		Returns the cost of the current state, which is equal to
# 		the sum of :
# 		nb_obstacles / (head_depth+tail_depth)
# 		for all extra arrows
# 		where nb_obstacles is
# 		the nb of vertices with same radius as head,
# 		between theta_head and theta_tail
# 		+
# 		the nb of vertices with same radius as tail,
# 		between theta_head and theta_tail
# 		'''
# 		cost = 0
# 		for arrow in self.current_state.extra_edges:
# 			r = [arrow.head.depth, arrow.tail.depth]
# 			theta = [arrow.head.angular_position(),
# 					 arrow.tail.angular_position()]
# 			start, end = None, None
# 			if (theta[0]-theta[1]) % (2*math.pi) < math.pi:
# 				start, end = theta[1], theta[0]
# 			else:
# 				start, end = theta[0], theta[1]

# 			circles = [self.current_state.tree.vertices_by_depth[r[0]]]
# 			if r[0] != r[1]:
# 				circles.append(self.current_state.tree.vertices_by_depth[r[1]])
						
# 			nb_obstacles = 0
# 			for circle in circles:
# 				for vertex in circle:
# 					if vertex != arrow.head and vertex != arrow.tail:
# 						angle = vertex.angular_position()
# 						if ((angle-start)%(2*math.pi) < math.pi and
# 							(end-angle)%(2*math.pi) < math.pi):
# 								nb_obstacles += 1

# 			if r[0] == r[1]:
# 				nb_obstacles *= 2

# 			cost += nb_obstacles/(r[0]+r[1])

# #			length = r1**2 + r2**2 - 2*r1*r2*math.cos(theta1-theta2)
# #			cost += length/(r1+r2) 
# 		return cost

# 	def currentCost2(self):
# 		'''
# 		Returns the cost of the current state, which is equal to
# 		the number of crossing edges +
# 		10 * the number of edge crossing a vertex +
# 		5 * the number of mingled edges

# 		'''
# 		print("\n\n\n")
# 		cost = 0
# 		r = 0.9 * self.current_state.tree.max_radius
# 		vertices_circles = []
# 		for vertex in self.current_state.tree.vertices:
# 			x = vertex.depth * math.cos(vertex.angular_position())
# 			y = vertex.depth * math.sin(vertex.angular_position())
# 			vertices_circles.append(Circle(x, y, r))

# 		for arrow in self.current_state.extra_edges:
# 			hx = arrow.head.depth * math.cos(arrow.head.angular_position())
# 			hy = arrow.head.depth * math.sin(arrow.head.angular_position())
# 			tx = arrow.tail.depth * math.cos(arrow.tail.angular_position())
# 			ty = arrow.tail.depth * math.sin(arrow.tail.angular_position())
# 			s1 = Segment(hx, hy, tx, ty)
# 			# Check edge crossing
# 			for arrow2 in self.current_state.extra_edges + self.current_state.tree.arrows:
# 				if arrow2 != arrow:
# 					hx2 = arrow2.head.depth * math.cos(arrow2.head.angular_position())
# 					hy2 = arrow2.head.depth * math.sin(arrow2.head.angular_position())
# 					tx2 = arrow2.tail.depth * math.cos(arrow2.tail.angular_position())
# 					ty2 = arrow2.tail.depth * math.sin(arrow2.tail.angular_position())
# 					s2 = Segment(hx2, hy2, tx2, ty2)
# 					inter = s1.intersectionWith(s2)

# 					# mingled segment
# 					if isinstance(inter, Segment):
# 						cost += 5
# 						print("Found segment ({},{}) and ({},{}) are mingled : +5".format(arrow.head.id, arrow.tail.id, arrow2.head.id, arrow2.tail.id))
# 					# single point intersection
# 					elif inter != None:
# 						cost += 1
# 						print("Found segment ({},{}) and ({},{}) intersect : +1".format(arrow.head.id, arrow.tail.id, arrow2.head.id, arrow2.tail.id))

# 			# Check vertex crossing
# 			for i in range(len(self.current_state.tree.vertices)):
# 				v = self.current_state.tree.vertices[i]
# 				c = vertices_circles[i]
# 				if v != arrow.head and v != arrow.tail:
# 					inter = c.intersectionWithSegment(s1)
# 					if inter != None:
# 						print("Found segment ({},{}) and Circle {} intersect : +10".format(arrow.head.id, arrow.tail.id, i))
# 						cost += 10

# 		return cost

	# def acceptanceProbability(self, old_cost, new_cost, temperature):
	# 	'''
	# 	Returns the acceptance probability of a new state of cost new_cost
	# 	if the cost of the previous state was old_cost and the temperature
	# 	is temperature
	# 	'''
	# 	proba = math.exp((old_cost-new_cost)/temperature)
	# 	return min(proba, 1)

	def goToNewState(self):
		'''
		Changes current_state to a random neighbour state
		(i.e. a layout obtained by switching two successors in one vertex)
		'''
		vertex = random.choice(self.current_state.tree.vertices)
		size = len(vertex.outgoing_arrows)
		if size > 1:
			index1 = random.randint(0, size-1)
			index2 = index1
			while index2 == index1:
				index2 = random.randint(0, size-1)
			tmp = vertex.outgoing_arrows[index1] 
			vertex.outgoing_arrows[index1] = vertex.outgoing_arrows[index2]
			vertex.outgoing_arrows[index2] = tmp
			self.current_state.tree.updateSectors()
			self.backtrack_info = BackTrackInfo(vertex, index1, index2)

	def goToPreviousState(self):
		'''
		If goToNewState() applied to previous_state gave current_state and
		returned backtrack_info, changes current_state to previous_state
		'''
		if self.backtrack_info != None:
			vertex = self.backtrack_info.vertex
			index1 = self.backtrack_info.index1
			index2 = self.backtrack_info.index2
			tmp = vertex.outgoing_arrows[index1] 
			vertex.outgoing_arrows[index1] = vertex.outgoing_arrows[index2]
			vertex.outgoing_arrows[index2] = tmp
			self.current_state.tree.updateSectors()


	def anneal(self):
		start = time.clock()
		old_cost = self.currentCost2()
		best_vertices = self.current_state.tree.vertices[:]
		smallest_cost = old_cost
		temperature = self.initial_temperature

		j = 0
		while time.clock() - start < self.time_limit:
			self.goToNewState()
			new_cost = self.currentCost2()
			if self.acceptanceProbability(old_cost, 
					new_cost, temperature) > random.random():
				old_cost = new_cost
				if new_cost < smallest_cost:
					smallest_cost = new_cost
					best_vertices = self.current_state.tree.vertices[:]
			else:
				self.goToPreviousState()

			j += 1
			if j % self.temperature_step == 0:
				temperature *= self.decrease_factor

		current_cost = self.currentCost2()
		if smallest_cost < current_cost:
			self.current_state.tree.vertices = best_vertices

		print("simulated annealing : {} iterations".format(j))
		print("cost : {}".format(min(current_cost, smallest_cost)))

		return self.current_state


class ArrowData:
	'''
	urls : String[]
	'''

	def __init__(self, urls):
		self.urls = urls


# class VertexData:
# 	'''
# 	label : String
# 	urls : String[]
# 	'''

# 	def __init__(self, label, urls):
# 		self.label = label
# 		self.urls = urls


# class Arrow:
# 	'''
# 	tail : Vertex
# 	head : Vertex
# 	data : ArrowData
# 	'''

# 	def __init__(self, tail, head, data):
# 		self.tail = tail
# 		self.head = head
# 		self.data = data

# 	def mergeData(self, arrow):
# 		'''
# 		Replaces this self's and arrow's data by their
# 		merged data 
# 		'''
# 		merged_urls = list(set(self.data.urls+arrow.data.urls))
# 		self.data.urls = arrow.data.urls = merged_urls

class Vertex:
	'''
	Graph related
	id : int
	outgoing_arrows : Arrow[]
	subtree_size : int
	depth : int

	Layout related
	sector_start : float (radians)
	sector_width : float (radians)
	'''

	def __init__(self, id):
		self.id = id
		self.outgoing_arrows = []
		self.subtree_size = 1
		self.depth = 0
		sector_start = 0
		sector_width = 0

	def angular_position(self):
		return self.sector_start + self.sector_width/2

	def updateDepthRecursively(self, depth):
		'''
		Sets this vertex's depth to depth then calls itself recursively
		on its successors with an incremented depth.
		'''
		self.depth = depth
		for arrow in self.outgoing_arrows:
			successor = arrow.head
			successor.updateDepthRecursively(depth+1)

	def updateSubtreeSizeRecursively(self):
		'''
		Calls itself recursively on this vertex's successors, to
		compute his subtree size then returns it.
		The subtree size is defined by the following rules:
		- the subtree size of a vertex without successor is 1 ;
		- the subtree size of a vertex with successors is the sum
		of their subtree sizes.
		'''
		if len(self.outgoing_arrows) == 0:
			self.subtree_size = 1
		else:
			self.subtree_size = 0
			for arrow in self.outgoing_arrows:
				successor = arrow.head
				self.subtree_size += successor.updateSubtreeSizeRecursively()
		return self.subtree_size

	def updateSectorRecursively(self, graph, sector_start, sector_width):
		'''
		Sets this vertex's angular attributes to angular_start and 
		angular_width, then computes its successors' angular attributes 
		according to the following rules to call itself recursively :
		- each vertex's angular sector is shared between its successors,
		in proportion to their subtree size.

		Also, if graph isn't None, compares this vertex 
		maximum radius (depth*sin(sector_width/2)) 
		to graph's current maximum radius and updates it if necessary

		Returns a 2-D array, containing the vertices of self's subtree, by depth,
		sorted by ascending angular position.
		result[0] contains self 
		result[1] contains its successors sorted by ascending angular position
		result[2] successors of its successors, etc.
		'''
		if graph != None:
			if sector_width < math.pi:
				self_max_radius = self.depth * math.sin(sector_width/2)
				graph.max_radius = min(graph.max_radius, self_max_radius)

		self.sector_start = sector_start
		self.sector_width = sector_width
		result = []
		result.append([self])
		for arrow in self.outgoing_arrows:
			successor = arrow.head
			# The share of a succesor is proportionnal to its subtree_size
			# And it cannot be higher than pi
			share = successor.subtree_size / self.subtree_size
			share *= sector_width
			share = min(math.pi, share)
			positions = successor.updateSectorRecursively(graph, sector_start, share)
			sector_start += share
			# Merge successor result with own result
			common_indices = min(len(result)-1, len(positions))
			for i in range(common_indices):
				result[i+1].extend(positions[i])
			for i in range(common_indices, len(positions)):
				result.append(positions[i]) 
		return result



class DirectedGraph:
	'''
	root : Vertex
	vertices : Vertex[]
	arrows : Arrow[]

	Layout related :
	vertices_by_depth : float[][]   
	(used for fastest cost computation, vertices_by_depth[d][i] 
	is the i-th vertex of the d-th depth)
	max_radius : float
	'''

	def __init__(self):
		self.root = None
		self.vertices = []
		self.arrows = []
		self.vertices_by_depth = [[]]
		self.max_radius = 0.5

	def updateDepths(self):
		'''
		Can be used only if the graph is a tree (i.e. has no cycle).
		
		Updates all vertices' depth, according to the following rules:
		- the depth of the root is 1
		- the depth of a vertex is the depth of its predecessor + 1
		'''
		if self.root != None:
			self.root.updateDepthRecursively(0)

	def updateSubtreeSizes(self):
		'''
		Can be used only if the graph is a tree (i.e. has no cycle).

		Updates all the vertices' subtree sizes, according to the 
		following rules:
		- the subtree size of a vertex without successor is 1 ;
		- the subtree size of a vertex with successors is 1 + the sum
		of their subtree sizes.
		'''
		if self.root != None:
			self.root.updateSubtreeSizeRecursively()

	def updateSectors(self):
		'''
		Can be used only if the graph is a tree (i.e. has no cycle),
		and depths and subtree sizes are up-to-date.
		
		Updates all vertices' angular attributes, according to the
		current outgoing_arrows orders and subtree_size values :
		- the root's angular sector is the (0, 2pi) ;
		- each vertex's angular sector is shared between its successors,
		in proportion to their subtree size.

		Also updates the maximum radius (to use to avoid vertices 
		overlapping)
		'''
		self.max_radius = 0.5
		if self.root != None:
			self.vertices_by_depth = self.root.updateSectorRecursively(self, 0, 2*math.pi)


class EnhancedTree:
	'''
	tree : DirectedGraph (must not have cycles)
	extra_edges : Arrow[]
	'''

	def undirect(self):
		'''
		Merge the data of each arrow into the reverse one, creating
		it if necessary
		''' 
		for arrow in self.tree.arrows:
			# Checks if the reverse arrow exists 
			reverse_arrow = None
			for arrow2 in arrow.head.outgoing_arrows:
				if arrow2.head == arrow.tail:
					reverse_arrow = arrow2
					break
			# Creates it if necessary
			if reverse_arrow == None:
				reverse_arrow = Arrow(arrow.head, arrow.tail, arrow.data)
				arrow.head.outgoing_arrows.append(reverse_arrow)
				self.tree.arrows.append(reverse_arrow)
			# If the data of the arrow and the reverse arrow
			# are different, changes them to a same merged set of data
			if arrow.data != reverse_arrow.data:
				arrow.mergeData(reverse_arrow)


	def __init__(self, inputGraph, root):
		self.tree = inputGraph
		self.tree.root = root
		self.extra_edges = []
		
		# Transforms the inputGraph into an undirected graph
		# by merging 2-way arrows into a double arrow with merge data
		self.undirect()

		# Performs a BFS to :
		# - detect cycling edges, and put them into extra_edges
		# - keep only one of the double arrows
		explored = [False] * len(self.tree.vertices)
		queue = [root]
		explored[root.id] = True
		while len(queue) > 0:
			vertex = queue.pop(0)
			i = 0
			while i < len(vertex.outgoing_arrows): 
				
				arrow = vertex.outgoing_arrows[i]
				successor = arrow.head
				# remove the reverse arrow
				for arrow2 in successor.outgoing_arrows:
					if arrow2.head == vertex:
						successor.outgoing_arrows.remove(arrow2)
						self.tree.arrows.remove(arrow2)
						break

				if explored[successor.id]:
					self.extra_edges.append(arrow)
					vertex.outgoing_arrows.remove(arrow)
					self.tree.arrows.remove(arrow)
				else:
					explored[successor.id] = True
					queue.append(successor)
					i += 1

		# Update attributes
		self.tree.updateDepths()
		self.tree.updateSubtreeSizes()
		self.tree.updateSectors()


	def display(self, unit=100, margin=100, radius_ratio=0.9):
		# Computes vertices coordinates in canvas
		x, y = [], []
		depth_max = 0
		xmin = ymin = sys.maxsize
		xmax = ymax = -sys.maxsize
		for vertex in self.tree.vertices:
			if vertex.depth > depth_max:
				depth_max = vertex.depth
			theta = vertex.sector_start + vertex.sector_width/2
			x.append(round(unit*vertex.depth * math.cos(theta)))
			y.append(round(unit*vertex.depth * math.sin(theta)))
			xmin = min(x[-1], xmin)
			xmax = max(x[-1], xmax)
			ymin = min(y[-1], ymin)
			ymax = max(y[-1], ymax)
		
		for i in range(len(x)):
			x[i] = x[i]-xmin + margin
			y[i] = y[i]-ymin + margin

		xmax = xmax-xmin + 2*margin 
		ymax = ymax-ymin + 2*margin
		im = Image.new("RGB", (xmax, ymax), "white")
		draw = ImageDraw.Draw(im)

		# Draw concentric circles
		cx, cy = x[self.tree.root.id], y[self.tree.root.id]
		for r in range(0, unit*(depth_max+1), unit):
			draw.ellipse((cx-r, cy-r, cx+r, cy+r), outline='grey')
		
		# draw tree arrows
		for arrow in self.tree.arrows:
			x1 = x[arrow.head.id]
			x2 = x[arrow.tail.id]
			y1 = y[arrow.head.id]
			y2 = y[arrow.tail.id]
			w = len(arrow.data.urls)
			draw.line((x1, y1, x2, y2), fill='blue', width=2*w)
			#draw.text(((x1+x2)/2, (y1+y2)/2), "{}".format(w), fill='black')

		# Draw vertices
		radius = radius_ratio * self.tree.max_radius * unit
		for i in range(len(x)):
			x1 = x[i] - radius
			x2 = x[i] + radius
			y1 = y[i] - radius
			y2 = y[i] + radius
			draw.ellipse((x1, y1, x2, y2), outline='black', fill='white')

			draw.text((x[i], y[i]), "{}".format(i), fill='black')

		# draw additional edges
		for edge in self.extra_edges:
			x1 = x[edge.head.id]
			x2 = x[edge.tail.id]
			y1 = y[edge.head.id]
			y2 = y[edge.tail.id]
			w = len(edge.data.urls)
			draw.line((x1, y1, x2, y2), fill='red', width=2*w)
			#draw.text(((x1+x2)/2, (y1+y2)/2), "{}".format(w), fill='black')
		

		im.show()

	def display2(self, unit=100, margin=100, radius_ratio=0.9):
		# Computes vertices coordinates in canvas
		x, y = [], []
		depth_max = 0
		xmin = ymin = sys.maxsize
		xmax = ymax = -sys.maxsize
		for vertex in self.tree.vertices:
			if vertex.depth > depth_max:
				depth_max = vertex.depth
			theta = vertex.sector_start + vertex.sector_width/2
			x.append(round(unit*vertex.depth * math.cos(theta)))
			y.append(round(unit*vertex.depth * math.sin(theta)))
			xmin = min(x[-1], xmin)
			xmax = max(x[-1], xmax)
			ymin = min(y[-1], ymin)
			ymax = max(y[-1], ymax)
		
		for i in range(len(x)):
			x[i] = x[i]-xmin + margin
			y[i] = y[i]-ymin + margin

		xmax = xmax-xmin + 2*margin 
		ymax = ymax-ymin + 2*margin
		im = Image.new("RGB", (xmax, ymax), "white")
		draw = ImageDraw.Draw(im)

		# Draw concentric circles
		cx, cy = x[self.tree.root.id], y[self.tree.root.id]
		for r in range(0, unit*(depth_max+1), unit):
			draw.ellipse((cx-r, cy-r, cx+r, cy+r), outline='grey')
		
		# draw tree arrows
		for arrow in self.tree.arrows:
			x1 = x[arrow.head.id]
			x2 = x[arrow.tail.id]
			y1 = y[arrow.head.id]
			y2 = y[arrow.tail.id]
			w = len(arrow.data.urls)
			draw.line((x1, y1, x2, y2), fill='white', width=2*w+2)
			draw.line((x1, y1, x2, y2), fill='blue', width=2*w)
			draw.text(((x1+x2)/2, (y1+y2)/2), "{}".format(w), fill='black')

		# draw additional edges
		for edge in self.extra_edges:
			x1 = x[edge.head.id]
			y1 = y[edge.head.id]
			# for arcs, angles are measured clockwise, in degrees
			theta1 = edge.head.sector_start + edge.head.sector_width/2
			theta1_deg = round(math.degrees(theta1))%360
			r1 = edge.head.depth


			x2 = x[edge.tail.id]
			y2 = y[edge.tail.id]
			theta2 = edge.tail.sector_start + edge.tail.sector_width/2
			theta2_deg = round(math.degrees(theta2))%360
			r2 = edge.tail.depth

			w = len(edge.data.urls)
			r = unit*(r1+r2)/2

			if (theta1_deg-theta2_deg) % 360 < 180:
				start = theta2_deg
				end = theta1_deg
			else:
				start = theta1_deg
				end = theta2_deg

			draw.arc((cx-r,cy-r,cx+r,cy+r), start, end, fill='red')

			x3 = cx + r*math.cos(theta1)
			y3 = cy + r*math.sin(theta1)
			draw.line((x1, y1, x3, y3), fill='red')
			x4 = cx + r*math.cos(theta2)
			y4 = cy + r*math.sin(theta2)
			draw.line((x2, y2, x4, y4), fill='red')
		
		# Draw vertices
		radius = radius_ratio * self.tree.max_radius * unit
		for i in range(len(x)):
			x1 = x[i] - radius
			x2 = x[i] + radius
			y1 = y[i] - radius
			y2 = y[i] + radius
			draw.ellipse((x1, y1, x2, y2), outline='black', fill='white')
			draw.text((x[i], y[i]), "{}".format(i), fill='black')

		im.show()

	def display3(self, unit=100, margin=100, radius_ratio=0.9):
		# Computes vertices coordinates in canvas
		x, y = [], []
		depth_max = 0
		for vertex in self.tree.vertices:
			depth_max = max(vertex.depth, depth_max)
			theta = vertex.sector_start + vertex.sector_width/2
			x.append(round(unit*vertex.depth * math.cos(theta)))
			y.append(round(unit*vertex.depth * math.sin(theta)))
		xmin = -depth_max*unit
		xmax = depth_max*unit
		ymin = -depth_max*unit
		ymax = depth_max*unit

		for i in range(len(x)):
			x[i] = x[i]-xmin + margin
			y[i] = y[i]-ymin + margin

		xmax = xmax-xmin + 2*margin 
		ymax = ymax-ymin + 2*margin
		im = Image.new("RGB", (xmax, ymax), "white")
		draw = ImageDraw.Draw(im)

		# Draw concentric circles
		cx, cy = x[self.tree.root.id], y[self.tree.root.id]
		for r in range(0, unit*(depth_max+1), unit):
			draw.ellipse((cx-r, cy-r, cx+r, cy+r), outline='grey')
		
		#draw tree arrows
		for arrow in self.tree.arrows:
			x1 = x[arrow.head.id]
			x2 = x[arrow.tail.id]
			y1 = y[arrow.head.id]
			y2 = y[arrow.tail.id]
			w = len(arrow.data.urls)
			draw.line((x1, y1, x2, y2), fill='blue', width=2*w)

		# for edge in self.tree.arrows:
		# 	theta, r = [], []
		# 	theta.append(edge.head.sector_start + edge.head.sector_width/2)
		# 	theta.append(edge.tail.sector_start + edge.tail.sector_width/2)
		# 	r.append(edge.head.depth*unit)
		# 	r.append(edge.tail.depth*unit)
		# 	w = len(edge.data.urls)

		# 	if abs(theta[0] - theta[1]) < 0.01:
		# 		x1 = x[edge.head.id]
		# 		x2 = x[edge.tail.id]
		# 		y1 = y[edge.head.id]
		# 		y2 = y[edge.tail.id]
		# 		w = len(edge.data.urls)
		# 		draw.line((x1, y1, x2, y2), fill='blue', width=2*w)
		# 	else:
		# 		# connect both ends the shortest way
		# 		start, end = None, None
		# 		if (theta[0]-theta[1]) % (2*math.pi) < math.pi:
		# 			start, end = 1, 0
		# 		else:
		# 			start, end = 0, 1

		# 		if theta[end] < theta[start]:
		# 			theta[end] += 2*math.pi

		# 		print("going from angle {} to angle {}".format(theta[start], theta[end]))
				
		# 		fast_in_out = lambda x: 0.5 + (math.tan(3*(x-0.5)))/(2*math.tan(1.5))

		# 		print(fast_in_out(0), fast_in_out(0.5), fast_in_out(1))

		# 		curr_theta = theta[start]
		# 		prev_x, prev_y = None, None
		# 		while curr_theta < theta[end]:
		# 			progress = fast_in_out((curr_theta-theta[start])/(theta[end]-theta[start]))
		# 			curr_r = r[start] + (r[end]-r[start]) * progress 
		# 			curr_x = cx + curr_r * math.cos(curr_theta)
		# 			curr_y = cy + curr_r * math.sin(curr_theta)
		# 			if prev_x != None:
		# 				draw.line((prev_x, prev_y, curr_x, curr_y), fill='blue', width=2*w)
		# 			prev_x, prev_y = curr_x, curr_y
		# 			curr_theta += 0.01


		# draw additional edges
		for edge in self.extra_edges:
			theta, r = [], []
			theta.append(edge.head.sector_start + edge.head.sector_width/2)
			theta.append(edge.tail.sector_start + edge.tail.sector_width/2)
			r.append(edge.head.depth*unit)
			r.append(edge.tail.depth*unit)
			w = len(edge.data.urls)

			if abs(theta[0] - theta[1]) < 0.01:
				x1 = x[edge.head.id]
				x2 = x[edge.tail.id]
				y1 = y[edge.head.id]
				y2 = y[edge.tail.id]
				w = len(edge.data.urls)
				draw.line((x1, y1, x2, y2), fill='red', width=2*w)
			else:
				# connect both ends the shortest way
				start, end = None, None
				if (theta[0]-theta[1]) % (2*math.pi) < math.pi:
					start, end = 1, 0
				else:
					start, end = 0, 1

				if theta[end] < theta[start]:
					theta[end] += 2*math.pi
				
				fast_in_out = lambda x: 0.5 + (math.tan(3.1*(x-0.5)))/(2*math.tan(1.55))

				curr_theta = theta[start]
				prev_x, prev_y = None, None
				while curr_theta < theta[end]:
					progress = fast_in_out((curr_theta-theta[start])/(theta[end]-theta[start]))
					curr_r = r[start] + (r[end]-r[start]) * progress 
					curr_x = cx + curr_r * math.cos(curr_theta)
					curr_y = cy + curr_r * math.sin(curr_theta)
					if prev_x != None:
						draw.line((prev_x, prev_y, curr_x, curr_y), fill='red', width=2*w)
					prev_x, prev_y = curr_x, curr_y
					curr_theta += 0.01

		# Draw vertices
		radius = radius_ratio * self.tree.max_radius * unit
		for i in range(len(x)):
			x1 = x[i] - radius
			x2 = x[i] + radius
			y1 = y[i] - radius
			y2 = y[i] + radius
			draw.ellipse((x1, y1, x2, y2), outline='black', fill='white')
			draw.text((x[i], y[i]), "{}".format(i), fill='black')

		im.show()



if __name__ == '__main__':

	# launch timer
	start = time.clock()

	# Pretty big graph
	v0, v1, v2 = Vertex(0), Vertex(1), Vertex(2)
	v3, v4, v5 = Vertex(3), Vertex(4), Vertex(5)
	v6, v7, v8 = Vertex(6), Vertex(7), Vertex(8)
	v9, v10, v11 = Vertex(9), Vertex(10), Vertex(11)
	v12, v13, v14 = Vertex(12), Vertex(13), Vertex(14)
	vertices = [v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14]
	v0.outgoing_arrows = [Arrow(v0, v5, ArrowData(["url", "url"])),
				   		  Arrow(v0, v1, ArrowData(["url1"])),
				   		  Arrow(v0, v12, ArrowData(["url1"])),
				   		  Arrow(v0, v13, ArrowData(["url1", "url"]))]
	v1.outgoing_arrows = [Arrow(v1, v2, ArrowData(["url1"])),
				   		  Arrow(v1, v3, ArrowData(["url1"])),
				   		  Arrow(v1, v4, ArrowData(["url1", "url"])),
				   		  Arrow(v1, v6, ArrowData(["url1"])),
				   		  Arrow(v1, v0, ArrowData(["url1"]))]
	v2.outgoing_arrows = [Arrow(v2, v1, ArrowData(["url1"])),
				   		  Arrow(v2, v10, ArrowData(["url1", "url"]))]
	v3.outgoing_arrows = [Arrow(v3, v1, ArrowData(["url1"]))]
	v4.outgoing_arrows = [Arrow(v4, v1, ArrowData(["url1"]))]
	v5.outgoing_arrows = [Arrow(v5, v6, ArrowData(["url1"])),
				   		  Arrow(v5, v9, ArrowData(["url1"]))]
	v6.outgoing_arrows = [Arrow(v6, v5, ArrowData(["url1"])),
				   		  Arrow(v6, v1, ArrowData(["url1", "url", "url"])),
				   		  Arrow(v6, v7, ArrowData(["url1"])),
				   		  Arrow(v6, v8, ArrowData(["url1"]))]
	v7.outgoing_arrows = [Arrow(v7, v6, ArrowData(["url1"]))]
	v8.outgoing_arrows = []
	v9.outgoing_arrows = [Arrow(v9, v5, ArrowData(["url1"])),
				   		  Arrow(v9, v10, ArrowData(["url1"])),
				   		  Arrow(v9, v11, ArrowData(["url1", "url"])),
				   		  Arrow(v9, v12, ArrowData(["url1"]))]
	v10.outgoing_arrows = [Arrow(v10, v9, ArrowData(["url1"])),
				    	   Arrow(v10, v2, ArrowData(["url1"]))]
	v11.outgoing_arrows = [Arrow(v11, v9, ArrowData(["url1", "url"])),
				    	   Arrow(v11, v14, ArrowData(["url1"]))]
	v12.outgoing_arrows = [Arrow(v12, v0, ArrowData(["url1"])),
				    	   Arrow(v12, v9, ArrowData(["url1", "url"]))]
	v13.outgoing_arrows = [Arrow(v13, v0, ArrowData(["url1"])),
				    	   Arrow(v13, v14, ArrowData(["url1"]))]
	v14.outgoing_arrows = [Arrow(v14, v13, ArrowData(["url1", "url"])),
				    	   Arrow(v14, v11, ArrowData(["url1"]))]
	arrows = (v0.outgoing_arrows + v1.outgoing_arrows + v2.outgoing_arrows +
			 v3.outgoing_arrows + v4.outgoing_arrows + v5.outgoing_arrows +
			 v6.outgoing_arrows + v7.outgoing_arrows + v8.outgoing_arrows +
			 v9.outgoing_arrows + v10.outgoing_arrows + v11.outgoing_arrows +
			 v12.outgoing_arrows + v13.outgoing_arrows + v14.outgoing_arrows)

	inputGraph = DirectedGraph()
	inputGraph.arrows = arrows
	inputGraph.vertices = vertices
	print("Input graph built in : {}s".format(time.clock()-start))

	start = time.clock()
	workingGraph = EnhancedTree(inputGraph, v3)
	print("Working graph built in : {}s".format(time.clock()-start))

	start = time.clock()
	annealSimulator = AnnealingSimulator(workingGraph, 2)
	annealSimulator.anneal()
	print("Layout computed in : {}s".format(time.clock()-start))
	workingGraph.display()
