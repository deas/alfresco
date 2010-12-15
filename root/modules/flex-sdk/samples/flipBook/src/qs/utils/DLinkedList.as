/*Copyright (c) 2006 Adobe Systems Incorporated

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/
package qs.utils
{
	public class DLinkedList
	{
		private var _head:DLinkedListNode;
		private var _tail:DLinkedListNode;
		private var _length:Number = 0;
		private var _nodeClass:Class;
		
		public function DLinkedList(nodeClass:Class = null):void
		{
			_nodeClass = (nodeClass == null)? DLinkedListNode:nodeClass;
			_head = new _nodeClass();
			_tail = new _nodeClass();
			_head.next = _tail;
			_tail.prev = _head;
		}
		public function get first():DLinkedListNode
		{
			return (_head.next == _tail)? null:_head.next;
		}
		public function get last():DLinkedListNode
		{
			return (_tail.prev == _head)? null:_head;
		}
		
		public function get tail():DLinkedListNode
		{
			return _tail;
		}
		public function get head():DLinkedListNode
		{
			return _head;
		}
		
		public function get length():Number
		{
			return _length;
		}
		
		private function makeNode(value:*):DLinkedListNode
		{
			var node:DLinkedListNode;
			if(value is DLinkedListNode)
			{
				node = value;
			}
			else
			{
				node = new _nodeClass(value);
			}
			return node;
		}

		public function insertAfter(value:*,prev:DLinkedListNode):DLinkedListNode
		{
			var node:DLinkedListNode = makeNode(value);
			node.prev = prev;
			node.next = prev.next;
			node.prev.next = node;
			node.next.prev = node;
			
			_length++;
			return node;
		}
		
		public function getNode(value:*):DLinkedListNode
		{
			if(value is DLinkedListNode)
				return value;
			else
			{
				return find(value);
			}
		}

		public function find(value:*):DLinkedListNode
		{
			var cur:DLinkedListNode = _head;
			while(cur.value != value && cur != _tail)
				cur = cur.next;
			return (cur == _tail)? null:cur;
		}
		
		public function remove(value:*):DLinkedListNode
		{
			var node:DLinkedListNode = getNode(value);
			node.prev.next = node.next;
			node.next.prev = node.prev;			
			_length--;
			return node;
		}

		public function push(value:*):DLinkedListNode
		{
			return insertAfter(value,_tail.prev);
		}

		public function pop():DLinkedListNode
		{
			return (_length == 0)? null:remove(_tail.prev);	
		}

		public function unshift(value:*):DLinkedListNode
		{
			return insertAfter(value,_head);
		}
		public function shift():DLinkedListNode
		{
			return (_length == 0)? null:remove(_head.next);
		}
	}
}